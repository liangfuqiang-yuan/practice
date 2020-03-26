package com.self.practice.handler;

import com.self.practice.bean.RpcRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class RpcServerHandler extends ChannelInboundHandlerAdapter{

    private Map<String,Object> handlerMap = new HashMap<>();

    public RpcServerHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //ctx写到客户端  msg从客户端读取数据
        RpcRequest rpcRequest = (RpcRequest) msg;

        Object result = new Object();
        if(handlerMap.containsKey(rpcRequest.getClassName())){
            Object clazz = handlerMap.get(rpcRequest.getClassName());
            Method method = clazz.getClass().getMethod(rpcRequest.getMethodName(),rpcRequest.getTypes());
            result = method.invoke(clazz,rpcRequest.getParams());
        }
        System.out.println("接收 ："+ msg);
        ctx.write(result);
        ctx.flush();
        ctx.close();
    }
}
