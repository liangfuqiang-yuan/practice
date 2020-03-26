package com.self.practice;

import com.self.practice.dto.UserDTO;
import com.self.practice.proxy.RpcClientProxy;
import com.self.practice.registry.IServiceDiscovery;
import com.self.practice.registry.IServiceDiscoveryImpl;
import com.self.practice.service.IPracticeDubboUserService;

public class ClientDemo {

    public static void main(String[] args) {
        IServiceDiscovery serviceDiscovery = new IServiceDiscoveryImpl();

        RpcClientProxy rpcClientProxy = new RpcClientProxy(serviceDiscovery);


        IPracticeDubboUserService userService = rpcClientProxy.creat(IPracticeDubboUserService.class);

        UserDTO u = userService.getUser(1);
        System.out.println("demo dto=" + u);

        String s = userService.getString("TOM");
        System.out.println("demo msg" + s);
    }
}
