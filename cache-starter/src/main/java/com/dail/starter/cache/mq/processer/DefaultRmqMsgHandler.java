package com.dail.starter.cache.mq.processer;

import com.dail.starter.cache.mq.IRmqMsgHandler;
import com.dail.starter.cache.mq.processer.command.CommandProcessChain;
import com.dail.starter.cache.mq.redis.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * description 默认广播消息处理器
 *
 * @author Dail 2023/01/03 17:28
 */
public class DefaultRmqMsgHandler implements IRmqMsgHandler {

    private static final Logger logger = LoggerFactory.getLogger(DefaultRmqMsgHandler.class);

    @Autowired
    private CommandProcessChain processChain;

    @Override
    public void doMessage(Message<?> message) {
        CommandProcessContext commandProcessContext = CommandProcessContext.initProcessContext(message.getCommand(),
                message.getBody(), message.getReplayKey());
        processChain.doProcess(commandProcessContext);
    }

}

