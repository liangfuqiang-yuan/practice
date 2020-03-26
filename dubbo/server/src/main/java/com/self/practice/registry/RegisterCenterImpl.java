package com.self.practice.registry;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

//服务注册到zk
public class RegisterCenterImpl implements IRegisterCenter {

    private CuratorFramework curatorFramework;

    {//连接到ZK
        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(ZkConfig.CONNECTION_URL).sessionTimeoutMs(4000)
                .retryPolicy(new ExponentialBackoffRetry(1000,10)).build();
        curatorFramework.start();
    }


    @Override
    public void register(String serviceName, String serviceAddress) {
        //服务注册地址
        String servicePath = ZkConfig.ZK_REGISTER_PATH + "/" + serviceName;

        try{
            // /registrys/com.self.practice.service.*Service 格式地址不存在
            if(curatorFramework.checkExists().forPath(servicePath) == null){
                //创建服务名称节点，持久化节点
                curatorFramework.create().creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT).forPath(servicePath,"0".getBytes());
            }
            String addressPath = servicePath + "/" + serviceAddress;
            //创建服务地址节点，临时节点（跟session绑定，服务不可用时剔除）,
            String node = curatorFramework.create().withMode(CreateMode.EPHEMERAL).forPath(addressPath,"0".getBytes());
            System.out.println("服务注册成功：" + node);
        }catch (Exception e){
            System.out.println(e);
        }

    }
}
