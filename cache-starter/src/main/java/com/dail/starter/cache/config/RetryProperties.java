package com.dail.starter.cache.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description 重试器配置文件
 *
 * @author Dail 2023/01/04 09:22
 */
@ConfigurationProperties(prefix = RetryProperties.PREFIX)
public class RetryProperties {

    public static final String PREFIX = "cache";

    private Retry retry = new Retry();

    public Retry getRetry() {
        return retry;
    }

    public void setRetry(Retry retry) {
        this.retry = retry;
    }

    public static class Retry {
        /**
         * 重试策略，默认固定时长
         */
        private String strategy = "fixed";
        /**
         * 默认5秒
         */
        private Long baseTime = 5L;
        /**
         * 递增时间
         */
        private Long maxTime;

        public String getStrategy() {
            return strategy;
        }

        public Retry setStrategy(String strategy) {
            this.strategy = strategy;
            return this;
        }

        public Long getBaseTime() {
            return baseTime;
        }

        public Retry setBaseTime(Long baseTime) {
            this.baseTime = baseTime;
            return this;
        }

        public Long getMaxTime() {
            return maxTime;
        }

        public Retry setMaxTime(Long maxTime) {
            this.maxTime = maxTime;
            return this;
        }
    }

}
