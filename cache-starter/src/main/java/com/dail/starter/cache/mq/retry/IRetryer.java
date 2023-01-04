package com.dail.starter.cache.mq.retry;

import com.github.rholder.retry.Retryer;

/**
 * description 通用重试器接口类
 *
 * @author Dail 2023/01/04 09:13
 */
public interface IRetryer {

    /**
     * 重试
     *
     * @param retryTimes 重试次数
     * @return 重试
     */
    Retryer<Void> buildRetry(Integer retryTimes);
}
