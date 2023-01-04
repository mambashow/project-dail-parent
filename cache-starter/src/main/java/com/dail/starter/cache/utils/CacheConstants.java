package com.dail.starter.cache.utils;

/**
 * description 缓存常量
 *
 * @author Dail 2023/01/04 08:25
 */
public interface CacheConstants {

    /**
     * 防止重复处理缓存的值，用简单的字符即可，没有实际含义
     */
    String REPLAY_VALUE = "Y";

    /**
     * 缓存Key模版
     */
    interface CacheKeyTemplate {
        /**
         * 防重复执行换成KEY replay:防重复KEY
         */
        String DELIVERY_REPLAY = "cache:replay:%s";
    }

    /**
     * 重试策略
     */
    interface RetryStrategy {
        /**
         * 固定
         */
        String FIXED = "fixed";
        /**
         * 递增
         */
        String INCREMENT = "increment";
        /**
         * 随机
         */
        String RANDOM = "random";
        /**
         * 斐波那契
         */
        String FIBONACCI = "fibonacci";
        /**
         * 指数
         */
        String EXPONENTIAL = "exponential";
    }
}
