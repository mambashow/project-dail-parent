package com.dail.starter.cache.multi;

import org.springframework.cache.Cache;

/**
 * description 多层缓存
 *
 * @author Dail 2023/01/03 15:42
 */
public abstract class MultiCache implements Cache {

    private final String name;

    public MultiCache(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public final Object getNativeCache() {
        return this;
    }

}

