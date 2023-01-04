package com.dail.starter.cache.mq.redis;

import com.github.rholder.retry.Retryer;
import com.dail.starter.cache.mq.retry.IRetryer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * description Redis消息队列发布器
 *
 * @author Dail 2023/01/04 09:12
 */
@Component
public class RedisPublisher {

    Logger logger = LoggerFactory.getLogger(RedisPublisher.class);

    private final RedisTemplate<String, String> redisTemplate;
    private final IRetryer retryer;

    public RedisPublisher(RedisTemplate<String, String> redisTemplate, IRetryer retryer) {
        this.redisTemplate = redisTemplate;
        this.retryer = retryer;
    }

    /**
     * 向通道发送消息的方法
     *
     * @param channel 消息通道
     * @param message 消息
     */
    public void sendChannelMessage(String channel, String message) {
        try {
            redisTemplate.convertAndSend(channel, message);
        } catch (Exception e) {
            logger.error("Message Publisher Error", e);
        }
    }

    /**
     * 向通道发送消息
     *
     * @param channel  消息通道
     * @param message  消息
     * @param tryTimes 重试次数
     */
    public void sendChannelMessage(String channel, String message, Integer tryTimes) {
        // 如未配置重试器，则直接发送渠道消息
        if (retryer == null) {
            this.sendChannelMessage(channel, message);
        } else {

            Retryer<?> retry = retryer.buildRetry(tryTimes);
            try {
                retry.call(() -> {
                    this.sendChannelMessage(channel, message);
                    return null;
                });
            } catch (Exception e) {
                // 出现异常则直接处理
                this.sendChannelMessage(channel, message);
            }
        }
    }

}

