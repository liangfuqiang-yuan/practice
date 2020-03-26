package com.self.practice.registry;

import com.self.practice.loadbalance.LoadBalance;
import com.self.practice.loadbalance.RandomLoadBalance;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取服务地址
 */
public class IServiceDiscoveryImpl implements IServiceDiscovery {

    List<String> repos = new ArrayList<String>();

    private CuratorFramework curatorFramework;

    public IServiceDiscoveryImpl(){
        //连接到ZK
        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(ZkConfig.CONNECTION_URL).sessionTimeoutMs(4000)
                .retryPolicy(new ExponentialBackoffRetry(1000,10)).build();
        curatorFramework.start();
    }

    @Override
    public String discover(String serviceName) {
        // 根据服务名称的zk地址urls，获取子节点，既服务地址
        String path = ZkConfig.ZK_REGISTER_PATH + "/" + serviceName;
        try{
            //服务有多个所以是list。（也可以将服务和地址缓存到本地）
            repos = curatorFramework.getChildren().forPath(path);

        }catch (Exception e){

        }

        //监听节点，当节点的子节点发送变化（服务提供地址变化，及时剔除或增加）
        registerWatch(path);

        //负载均衡算法
        LoadBalance loadBalance = new RandomLoadBalance();
        return loadBalance.select(repos);
    }

    private void registerWatch(final String path) {
        PathChildrenCache childrenCache = new PathChildrenCache(curatorFramework,path,true);
        PathChildrenCacheListener childrenCacheListener = new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                repos = curatorFramework.getChildren().forPath(path);
            }
        };
        childrenCache.getListenable().addListener(childrenCacheListener);
        try {
            childrenCache.start();
        } catch (Exception e) {
            throw new RuntimeException("测试监听异常：" + e);
        }
    }
}
