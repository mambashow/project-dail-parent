package com.dail.starter.cache.mq.redis;

import com.alibaba.fastjson.JSON;
import com.dail.starter.cache.mq.IRmqMsgHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * description Redis队列消息接收器(刷新缓存)
 *
 * @author Dail 2023/01/03 17:06
 */
@Component
public class RedisReceiver {

    Logger logger = LoggerFactory.getLogger(RedisReceiver.class);

    private final IRmqMsgHandler rmqMsgHandler;

    public RedisReceiver(IRmqMsgHandler rmqMsgHandler) {
        this.rmqMsgHandler = rmqMsgHandler;
    }

    /**
     * 通知消息
     *
     * @param body 消息内容
     */
    public void receiveMessage(String body) {
        Message<?> message = JSON.parseObject(body, Message.class);
        rmqMsgHandler.doMessage(message);
        logger.debug("Redis receive message:{}", message);
    }
}
