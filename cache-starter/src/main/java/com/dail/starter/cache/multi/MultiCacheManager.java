package com.dail.starter.cache.multi;

import com.dail.starter.cache.config.MultiCacheProperties;
import com.dail.starter.cache.l1.L1CacheManager;
import com.dail.starter.cache.l2.L2CacheManager;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCache;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * description 多层缓存管理器
 *
 * @author Dail 2023/01/03 15:42
 */
public class MultiCacheManager implements CacheManager {

    private final ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap<>(16);
    private final ConcurrentMap<String, Cache> l1CacheMap = new ConcurrentHashMap<>(16);
    private final ConcurrentMap<String, Cache> l2CacheMap = new ConcurrentHashMap<>(16);

    private final L1CacheManager l1CacheManager;

    private final L2CacheManager l2CacheManager;

    private final MultiCacheProperties properties;

    public MultiCacheManager(L1CacheManager l1CacheManager,
                             L2CacheManager l2CacheManager,
                             MultiCacheProperties properties) {
        this.l1CacheManager = l1CacheManager;
        this.l2CacheManager = l2CacheManager;
        this.properties = properties;
    }


    @Override
    public Cache getCache(String name) {
        Cache cache = this.cacheMap.get(name);
        if (cache == null) {
            synchronized (this.cacheMap) {
                cache = this.cacheMap.get(name);
                if (cache == null) {
                    cache = createMultiCacheByProperties(name);
                    cacheMap.put(name, cache);
                }
            }
        }
        return cache;
    }

    /**
     * 获取一级缓存
     *
     * @param name 名称
     * @return Cache
     */
    public Cache getL1Cache(String name) {
        Cache cache = this.l1CacheMap.get(name);
        if (cache == null) {
            synchronized (this.l1CacheMap) {
                cache = this.l1CacheMap.get(name);
                if (cache == null) {
                    cache = createL1CacheByProperties(name);
                    l1CacheMap.put(name, cache);
                }
            }
        }
        return cache;
    }

    /**
     * 获取二级缓存
     *
     * @param name 名称
     * @return Cache
     */
    public Cache getL2Cache(String name) {
        Cache cache = this.l2CacheMap.get(name);
        if (cache == null) {
            synchronized (this.l2CacheMap) {
                cache = this.l2CacheMap.get(name);
                if (cache == null) {
                    cache = createL2CacheByProperties(name);
                    l2CacheMap.put(name, cache);
                }
            }
        }
        return cache;
    }

    private Cache createMultiCacheByProperties(String name) {
        MultiCacheProperties.Cache config = properties.getCaches().get(name);
        if (config == null) {
            config = new MultiCacheProperties.Cache();
        }
        if (config.isL1Enabled() && config.isL2Enabled() && l1CacheManager != null && l2CacheManager != null) {
            return new MultiAllCache(name, l1CacheManager.getL1Cache(name, config.getL1Spec()),
                    l2CacheManager.getL2Cache(name, config.getL2Spec()));
        }
        if (config.isL1Enabled() && config.isL2Enabled() && l1CacheManager != null) {
            return new MultiL1Cache(name, l1CacheManager.getL1Cache(name, config.getL1Spec()));
        }
        if (config.isL1Enabled() && config.isL2Enabled() && l2CacheManager != null) {
            return new MultiL2Cache(name, l2CacheManager.getL2Cache(name, config.getL2Spec()));
        }
        if (config.isL1Enabled() && !config.isL2Enabled() && l1CacheManager != null) {
            return new MultiL1Cache(name, l1CacheManager.getL1Cache(name, config.getL1Spec()));
        }
        if (!config.isL1Enabled() && config.isL2Enabled() && l2CacheManager != null) {
            return new MultiL2Cache(name, l2CacheManager.getL2Cache(name, config.getL2Spec()));
        }
        return new NoOpCache(name);
    }

    private Cache createL1CacheByProperties(String name) {
        MultiCacheProperties.Cache config = properties.getCaches().get(name);
        if (config == null) {
            config = new MultiCacheProperties.Cache();
        }
        if (config.isL1Enabled() && l1CacheManager != null) {
            return l1CacheManager.getL1Cache(name, config.getL1Spec()).getCache();
        }
        return new NoOpCache(name);
    }

    private Cache createL2CacheByProperties(String name) {
        MultiCacheProperties.Cache config = properties.getCaches().get(name);
        if (config == null) {
            config = new MultiCacheProperties.Cache();
        }
        if (config.isL2Enabled() && l2CacheManager != null) {
            return l2CacheManager.getL2Cache(name, config.getL2Spec()).getCache();
        }
        return new NoOpCache(name);
    }

    @Override
    public Collection<String> getCacheNames() {
        return Collections.unmodifiableSet(this.cacheMap.keySet());
    }

}

