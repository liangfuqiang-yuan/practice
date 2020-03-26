package com.self.practice;


import com.self.practice.registry.IRegisterCenter;
import com.self.practice.registry.RegisterCenterImpl;

import java.io.IOException;

public class App {
    public static void main( String[] args ) throws IOException {
        IRegisterCenter registerCenter = new RegisterCenterImpl();
        registerCenter.register("com.self.practice.service.IPracticeDubboUserService","127.0.0.1:8080");
        System.in.read();
    }
}
