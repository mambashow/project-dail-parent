package com.dail.starter.cache.mq.retry;

import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategy;

/**
 * description 默认重试器实现类
 *
 * @author Dail 2023/01/04 09:17
 */
public class DefaultRetryer implements IRetryer {

    private final WaitStrategy waitStrategy;

    public DefaultRetryer(WaitStrategy waitStrategy) {
        this.waitStrategy = waitStrategy;
    }

    @Override
    public Retryer<Void> buildRetry(Integer retryTimes) {
        retryTimes = retryTimes == null ? 0 : retryTimes;
        return RetryerBuilder.<Void>newBuilder()
                // 如果异常会重试
                .retryIfException()
                // 重调策略
                .withWaitStrategy(waitStrategy)
                // 尝试次数
                .withStopStrategy(StopStrategies.stopAfterAttempt(retryTimes + 1))
                .build();
    }
}
