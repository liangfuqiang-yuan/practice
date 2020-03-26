package com.self.practice.registry;

public interface IServiceDiscovery {
    /**
     * 通过服务名称获取服务地址
     * @param serviceName
     * @return
     */
    String discover(String serviceName);
}
