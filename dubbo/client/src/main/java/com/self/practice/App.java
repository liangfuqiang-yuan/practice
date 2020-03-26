package com.self.practice;


import com.self.practice.registry.IServiceDiscovery;
import com.self.practice.registry.IServiceDiscoveryImpl;

public class App
{
    public static void main( String[] args )
    {
        IServiceDiscovery serviceDiscovery = new IServiceDiscoveryImpl();
        String path = serviceDiscovery.discover("com.self.practice.service.IPracticeDubboUserService");
        System.out.println("服务地址：" + path);
    }
}
