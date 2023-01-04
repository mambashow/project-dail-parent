package com.dail.starter.cache.l1.caffeine;

import com.dail.starter.cache.l1.L1Cache;
import com.dail.starter.cache.l1.L1CacheManager;
import org.springframework.cache.Cache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.util.StringUtils;

/**
 * description Caffeine一级缓存管理器
 *
 * @author Dail 2023/01/03 15:30
 */
public class CaffeineL1CacheManager extends CaffeineCacheManager implements L1CacheManager {

    private static final String CACHE_TYPE_CAFFEINE = "caffeine";

    @Override
    public L1Cache getL1Cache(String name, String spec) {
        synchronized (this) {
            if (StringUtils.hasText(spec)) {
                this.setCacheSpecification(spec);
            }
            Cache cache = this.getCache(name);
            if (cache != null) {
                return new CaffeineL1Cache(cache);
            }
            return null;
        }
    }

    public static String type() {
        return CACHE_TYPE_CAFFEINE;
    }

}

