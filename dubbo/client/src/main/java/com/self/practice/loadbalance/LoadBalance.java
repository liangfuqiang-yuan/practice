package com.self.practice.loadbalance;

import java.util.List;

/**
 * 负载均衡算法
 * 可实现随机、hash、轮询等算法
 */
public interface LoadBalance {
    String select(List<String> repos);
}
