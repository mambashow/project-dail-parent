package com.dail.starter.cache.utils;

/**
 * description 通用常量类
 *
 * @author Dail 2023/01/04 08:58
 */
public interface BaseConstants {
    /**
     * Redis消息队列指令
     */
    interface RmqCommand {
        /**
         * 刷新设备删除指令
         */
        String REFRESH_TEST_CACHE = "RTC";
    }

    /**
     * 二级缓存名称
     */
    interface CacheName {
        /**
         * 元数据缓存名称
         */
        String CACHE_TEST_NAME = "cache-test";
        /**
         * 消息处理防重放缓存
         */
        String REPLAY_CACHE_NAME = "replay";
    }

    /**
     * 缓存Key模版
     */
    interface CacheKeyTemplate {
        /**
         * 缓存测试
         */
        String CACHE_TEST = "cache:test:%s";
    }
}
