package com.dail.starter.cache.l1.caffeine;

import com.dail.starter.cache.l1.L1Cache;
import org.springframework.cache.Cache;

/**
 * description Caffeine一级缓存
 *
 * @author Dail 2023/01/03 15:28
 */
public class CaffeineL1Cache extends L1Cache {

    public CaffeineL1Cache(Cache cache) {
        super(cache);
    }

}

