package com.dail.starter.cache.config;

import com.github.rholder.retry.WaitStrategies;
import com.github.rholder.retry.WaitStrategy;
import com.dail.starter.cache.mq.retry.DefaultRetryer;
import com.dail.starter.cache.mq.retry.IRetryer;
import com.dail.starter.cache.utils.CacheConstants;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * description 消息发布重试配置类
 *
 * @author Dail 2023/01/04 09:21
 */
@Configuration
@EnableConfigurationProperties({RetryProperties.class})
public class RetryAutoConfiguration {

    @Bean
    @ConditionalOnProperty(name = "cache.retry.strategy")
    @ConditionalOnMissingBean(WaitStrategy.class)
    public WaitStrategy waitStrategy(RetryProperties retryProperties) {
        String strategy = retryProperties.getRetry().getStrategy();
        Long baseTime = retryProperties.getRetry().getBaseTime();
        Long maxTime = retryProperties.getRetry().getMaxTime();
        switch (strategy) {
            case CacheConstants.RetryStrategy.INCREMENT:
                return WaitStrategies.incrementingWait(baseTime, TimeUnit.SECONDS, maxTime, TimeUnit.SECONDS);
            case CacheConstants.RetryStrategy.RANDOM:
                return WaitStrategies.randomWait(baseTime, TimeUnit.SECONDS, maxTime, TimeUnit.SECONDS);
            case CacheConstants.RetryStrategy.FIBONACCI:
                return WaitStrategies.fibonacciWait(baseTime, maxTime, TimeUnit.SECONDS);
            case CacheConstants.RetryStrategy.EXPONENTIAL:
                return WaitStrategies.exponentialWait(baseTime, maxTime, TimeUnit.SECONDS);
            default:
                return WaitStrategies.fixedWait(baseTime, TimeUnit.SECONDS);
        }
    }

    @Bean
    @ConditionalOnMissingBean(DefaultRetryer.class)
    @ConditionalOnBean(WaitStrategy.class)
    public IRetryer DefaultRetryer(WaitStrategy waitStrategy) {
        return new DefaultRetryer(waitStrategy);
    }
}
