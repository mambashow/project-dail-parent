package com.dail.starter.cache.l2;

import org.springframework.cache.CacheManager;

/**
 * description 二级缓存管理器
 *
 * @author Dail 2023/01/03 15:28
 */
public interface L2CacheManager extends CacheManager {

    /**
     * 获取二级缓存
     *
     * @param name 名称
     * @param spec 配置
     * @return L1Cache
     */
    L2Cache getL2Cache(String name, String spec);

}

