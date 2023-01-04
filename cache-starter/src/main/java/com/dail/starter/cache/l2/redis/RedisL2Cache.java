package com.dail.starter.cache.l2.redis;

import com.dail.starter.cache.l2.L2Cache;
import org.springframework.cache.Cache;

/**
 * description Redis二级缓存
 *
 * @author Dail 2023/01/03 15:39
 */
public class RedisL2Cache extends L2Cache {

    public RedisL2Cache(Cache cache) {
        super(cache);
    }

}

