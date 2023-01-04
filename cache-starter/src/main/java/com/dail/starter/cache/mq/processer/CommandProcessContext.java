package com.dail.starter.cache.mq.processer;

/**
 * description 队列消息处理上下文
 *
 * @author Dail 2023/01/03 17:30
 */
public class CommandProcessContext {

    public static CommandProcessContext initProcessContext(String command, Object body, String replayKey) {
        return new CommandProcessContext(command, body, replayKey);
    }

    /**
     * 指令
     */
    private String command;
    /**
     * 消息体
     */
    private Object body;
    /**
     * 防重放KEY，用于幂等性处理
     */
    private String replayKey;

    public CommandProcessContext(String command, Object body, String replayKey) {
        this.command = command;
        this.body = body;
        this.replayKey = replayKey;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public String getReplayKey() {
        return replayKey;
    }

    public void setReplayKey(String replayKey) {
        this.replayKey = replayKey;
    }
}

