package com.dail.starter.cache.mq.redis;

import java.io.Serializable;

/**
 * description 广播消息对象
 *
 * @author Dail 2023/01/03 17:07
 */
public class Message<T> implements Serializable {
    /**
     * 指令
     */
    private String command;
    /**
     * 消息体
     */
    private T body;
    /**
     * 防重放KEY，用于幂等性处理
     */
    private String replayKey;

    public String getCommand() {
        return command;
    }

    public Message<T> setCommand(String command) {
        this.command = command;
        return this;
    }

    public T getBody() {
        return body;
    }

    public Message<T> setBody(T body) {
        this.body = body;
        return this;
    }

    public String getReplayKey() {
        return replayKey;
    }

    public Message<T> setReplayKey(String replayKey) {
        this.replayKey = replayKey;
        return this;
    }

    @Override
    public String toString() {
        return "Message{" +
                "command='" + command + '\'' +
                ", body='" + body + '\'' +
                ", replayKey='" + replayKey + '\'' +
                '}';
    }

}

