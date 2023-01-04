package com.dail.starter.cache.l1;

import org.springframework.cache.Cache;

/**
 * description 一级缓存
 *
 * @author Dail 2023/01/03 15:27
 */
public abstract class L1Cache {

    private final Cache cache;

    public L1Cache(Cache cache) {
        this.cache = cache;
    }

    public Cache getCache() {
        return cache;
    }

}

