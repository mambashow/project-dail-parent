package com.dail.starter.cache.mq;

import com.dail.starter.cache.mq.redis.Message;

/**
 * description 消息队列消息处理器接口
 *
 * @author Dail 2023/01/03 17:27
 */
@FunctionalInterface
public interface IRmqMsgHandler {

    /**
     * 处理二级制数据
     *
     * @param message 消息队列消息
     */
    void doMessage(Message<?> message);

}

