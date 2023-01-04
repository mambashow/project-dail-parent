package com.dail.starter.cache.processer.command.handler;

import com.dail.starter.cache.mq.processer.CommandProcessContext;
import com.dail.starter.cache.mq.processer.command.ICommandProcessHandler;
import com.dail.starter.cache.multi.MultiCacheManager;
import com.dail.starter.cache.utils.BaseConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;

/**
 * description 缓存清除测试消息处理器
 *
 * @author Dail 2023/01/04 08:53
 */
@Slf4j
public class CacheTestProcessHandler implements ICommandProcessHandler {

    @Autowired
    private MultiCacheManager cacheManager;

    @Override
    public int handlerOrder() {
        return 10;
    }

    @Override
    public boolean shouldHandler(CommandProcessContext context) {
        return isShould(context, BaseConstants.RmqCommand.REFRESH_TEST_CACHE);
    }

    @Override
    public boolean run(CommandProcessContext context) {
        Cache l1Cache = cacheManager.getL1Cache(BaseConstants.CacheName.REPLAY_CACHE_NAME);
        if (!isProcess(l1Cache, context.getReplayKey())) {
            return false;
        }
        Cache cache = cacheManager.getCache(BaseConstants.CacheName.CACHE_TEST_NAME);
        assert cache != null;
        switch (context.getCommand()) {
            case BaseConstants.RmqCommand.REFRESH_TEST_CACHE:
                refreshTestCache(cache, context.getBody());
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 刷新测试缓存
     *
     * @param cache 缓存
     * @param key   缓存KEY
     */
    private void refreshTestCache(Cache cache, Object key) {
        log.info("刷新缓存:{}", key);
        String keyFormat = String.format(BaseConstants.CacheKeyTemplate.CACHE_TEST, key);
        cache.evict(keyFormat);
    }
}
