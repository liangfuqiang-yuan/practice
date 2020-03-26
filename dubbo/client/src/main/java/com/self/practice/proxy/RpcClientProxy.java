package com.self.practice.proxy;

import com.self.practice.bean.RpcRequest;
import com.self.practice.registry.IServiceDiscovery;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


public class RpcClientProxy {
    private IServiceDiscovery serviceDiscovery;
    public RpcClientProxy(IServiceDiscovery serviceDiscovery){
        this.serviceDiscovery = serviceDiscovery;
    }

    public <T> T creat(final Class<T> interfaceClass){
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),new Class<?>[]{interfaceClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                RpcRequest rpcRequest = new RpcRequest();
                rpcRequest.setClassName(method.getDeclaringClass().getName());
                rpcRequest.setMethodName(method.getName());
                rpcRequest.setTypes(method.getParameterTypes());
                rpcRequest.setParams(args);

                String serviceName = interfaceClass.getName();
                String serviceAddress = serviceDiscovery.discover(serviceName);
                String[] address = serviceAddress.split(":");
                String host = address[0];
                int port = Integer.parseInt(address[1]);

                final RpcProxyHandler rpcProxyHandler = new RpcProxyHandler();
                EventLoopGroup group =  new NioEventLoopGroup();
                try{
                    Bootstrap b = new Bootstrap();
                    b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY,true)
                            .handler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel channel) throws Exception {
                                    ChannelPipeline pipeline = channel.pipeline();
                                    pipeline.addLast("frameDecoder",new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4));
                                    pipeline.addLast("frameEncoder",new LengthFieldPrepender(4));
                                    pipeline.addLast("encoder",new ObjectEncoder());
                                    pipeline.addLast("decoder",new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));

                                    //自定义符合netty标准的handler
                                    pipeline.addLast(rpcProxyHandler);
                                }
                            });
                    ChannelFuture channelFuture = b.connect(host,port).sync();
                    channelFuture.channel().writeAndFlush(rpcRequest);
                    channelFuture.channel().closeFuture().sync();
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    group.shutdownGracefully();
                }

                return rpcProxyHandler.getResponse();
            }
        });
    }
}
