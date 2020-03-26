package com.self.practice.loadbalance;

import java.util.List;
import java.util.Random;

/**
 * 负载均衡随机算法
 */
public class RandomLoadBalance implements LoadBalance {
    @Override
    public String select(List<String> repos) {
        int size = repos.size();
        Random random = new Random();
        return repos.get(random.nextInt(size));
    }
}
