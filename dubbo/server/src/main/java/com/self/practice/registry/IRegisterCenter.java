package com.self.practice.registry;

public interface IRegisterCenter {
    /**
     * 服务注册
     * @param serviceName 接口名称
     * @param serviceAddress 接口地址
     */
    void register(String serviceName,String serviceAddress);
}
