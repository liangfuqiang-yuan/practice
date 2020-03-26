package com.self.practice.server;

import com.self.practice.annotation.RpcAnnotation;
import com.self.practice.handler.RpcServerHandler;
import com.self.practice.registry.IRegisterCenter;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.util.HashMap;
import java.util.Map;

public class RpcServer {

    private IRegisterCenter registerCenter;
    private String serviceAddress;
    private Map<String,Object> handlerMap = new HashMap<>();

    public RpcServer(IRegisterCenter registerCenter,String serviceAddress){
        this.registerCenter = registerCenter;
        this.serviceAddress = serviceAddress;
    }

    public void publisher(){
        //注册
        for(String serviceName : handlerMap.keySet()){
            registerCenter.register(serviceName,serviceAddress);
        }
        // 监听通讯
        try{
            EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup workerLoopGroup = new NioEventLoopGroup();
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup,workerLoopGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel channel) throws Exception {
                    ChannelPipeline pipeline = channel.pipeline();
                    pipeline.addLast("frameDecoder",new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4));
                    pipeline.addLast("frameEncoder",new LengthFieldPrepender(4));
                    pipeline.addLast("encoder",new ObjectEncoder());
                    pipeline.addLast("decoder",new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));

                    //自定义符合netty标准的handler
                    pipeline.addLast(new RpcServerHandler(handlerMap));
                }
            }).option(ChannelOption.SO_BACKLOG,128)
              .childOption(ChannelOption.SO_KEEPALIVE,true);

            String[] addrs = serviceAddress.split(":");
            String ip = addrs[0];
            int port = Integer.valueOf(addrs[1]);
            ChannelFuture channelFuture = bootstrap.bind(ip,port).sync();
            System.out.println("服务启动成功，等待连接！");
            channelFuture.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void bind(Object... services){
        for(Object service : services){
            RpcAnnotation annotation = service.getClass().getAnnotation(RpcAnnotation.class);
            String serviceName = annotation.value().getName();
            handlerMap.put(serviceName,service);

        }
    }
}
