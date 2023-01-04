package com.dail.starter.cache.mq.processer.command;

import com.dail.starter.cache.mq.processer.CommandProcessContext;
import com.dail.starter.cache.utils.CacheConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.Cache;

/**
 * description 队列消息处理器抽象接口
 *
 * @author Dail 2023/01/03 17:30
 */
public interface ICommandProcessHandler {
    /**
     * handler顺序，越小越先执行
     *
     * @return handler顺序
     */
    int handlerOrder();

    /**
     * 是否执行
     *
     * @param context 数据处理上下文
     * @return true则执行，false不执行
     */
    boolean shouldHandler(CommandProcessContext context);

    /**
     * 执行方法
     *
     * @param context 数据处理上下文
     * @return true则继续执行后面的handler，false不再执行
     */
    boolean run(CommandProcessContext context);

    /**
     * 是否处理
     *
     * @param context 处理数据上下文
     * @param command 指令代码
     * @return 布尔值
     */
    default boolean isShould(CommandProcessContext context, String command) {
        String msgCommand = context.getCommand();
        return StringUtils.isNotBlank(command) && msgCommand.equals(command);
    }

    /**
     * 是否处理
     *
     * @param context  处理数据上下文
     * @param commands 指令代码
     * @return 布尔值
     */
    default boolean isShould(CommandProcessContext context, String... commands) {
        String msgCommand = context.getCommand();
        for (String command : commands) {
            if (msgCommand.equals(command)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 根据防重KEY,检查是否需要处理
     *
     * @param cache     缓存
     * @param replayKey 防重复KEY
     * @return 是否
     */
    default boolean isProcess(Cache cache, String replayKey) {
        if (StringUtils.isBlank(replayKey)) {
            return true;
        }
        if (cache != null) {
            String cacheKey = String.format(CacheConstants.CacheKeyTemplate.DELIVERY_REPLAY, replayKey);
            String cacheKeyValue = cache.get(cacheKey, String.class);
            if (StringUtils.isBlank(cacheKeyValue)) {
                cache.put(cacheKey, CacheConstants.REPLAY_VALUE);
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

}
