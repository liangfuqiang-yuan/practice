package com.self.practice;

import com.self.practice.registry.IRegisterCenter;
import com.self.practice.registry.RegisterCenterImpl;
import com.self.practice.server.RpcServer;
import com.self.practice.service.IPracticeDubboUserService;
import com.self.practice.service.impl.PracticeDubboUserServiceImpl;

public class ServerDemo {
    public static void main(String[] args) {
        IPracticeDubboUserService userService = new PracticeDubboUserServiceImpl();
        IRegisterCenter registerCenter = new RegisterCenterImpl();

        RpcServer rpcServer = new RpcServer(registerCenter,"127.0.0.1:8888");
        rpcServer.bind(userService);
        rpcServer.publisher();
    }
}
