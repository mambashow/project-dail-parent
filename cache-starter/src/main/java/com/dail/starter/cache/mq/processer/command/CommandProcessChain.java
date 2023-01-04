package com.dail.starter.cache.mq.processer.command;

import com.dail.starter.cache.mq.processer.CommandProcessContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * description 队列消息处理链管理器
 *
 * @author Dail 2023/01/03 17:29
 */
public class CommandProcessChain {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandProcessChain.class);

    private final List<ICommandProcessHandler> processHandlers;

    public CommandProcessChain(Optional<List<ICommandProcessHandler>> optionalProcessHandlers) {
        processHandlers = optionalProcessHandlers.orElseGet(Collections::emptyList)
                .stream()
                .sorted(Comparator.comparing(ICommandProcessHandler::handlerOrder))
                .collect(Collectors.toList());
    }

    public void doProcess(CommandProcessContext processContext) {
        try {
            for (ICommandProcessHandler processHandler : processHandlers) {
                if (processHandler.shouldHandler(processContext) && !processHandler.run(processContext)) {
                    break;
                }
            }
        } catch (Exception e) {
            LOGGER.info("Command Process error", e);
        }
    }
}

