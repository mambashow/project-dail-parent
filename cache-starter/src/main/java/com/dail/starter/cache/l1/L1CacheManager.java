package com.dail.starter.cache.l1;

import org.springframework.cache.CacheManager;

/**
 * description 一级缓存管理器
 *
 * @author Dail 2023/01/03 15:28
 */
public interface L1CacheManager extends CacheManager {

    /**
     * 获取一级缓存
     *
     * @param name 名称
     * @param spec 配置
     * @return L1Cache
     */
    L1Cache getL1Cache(String name, String spec);

}

