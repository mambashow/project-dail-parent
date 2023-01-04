package com.dail.starter.cache.l2;

import org.springframework.cache.Cache;

/**
 * description 二级缓存抽象类
 *
 * @author Dail 2023/01/03 15:27
 */
public abstract class L2Cache {

    private final Cache cache;

    public L2Cache(Cache cache) {
        this.cache = cache;
    }

    public Cache getCache() {
        return cache;
    }

}

