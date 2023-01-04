package com.dail.starter.cache.autoconfigure;

import com.github.rholder.retry.WaitStrategy;
import com.dail.starter.cache.config.MultiCacheAutoConfiguration;
import com.dail.starter.cache.config.ProcessHandlerConfiguration;
import com.dail.starter.cache.config.RetryAutoConfiguration;
import com.dail.starter.cache.mq.processer.command.CommandProcessChain;
import com.dail.starter.cache.mq.processer.command.ICommandProcessHandler;
import com.dail.starter.cache.mq.retry.DefaultRetryer;
import com.dail.starter.cache.mq.retry.IRetryer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

/**
 * description 服务组件自动化配置类
 *
 * @author Dail 2023/01/04 08:42
 */
@ComponentScan(value = {"com.dail"})
@Configuration
@Import({ProcessHandlerConfiguration.class, MultiCacheAutoConfiguration.class, RetryAutoConfiguration.class})
public class CacheAutoConfiguration {

    /**
     * 数据处理链
     *
     * @param optionalProcessHandlers 数据处理器
     * @return 数据处理链实例
     */
    @Bean
    public CommandProcessChain commandProcessChain(Optional<List<ICommandProcessHandler>> optionalProcessHandlers) {
        return new CommandProcessChain(optionalProcessHandlers);
    }

    @Bean
    @ConditionalOnMissingBean(DefaultRetryer.class)
    @ConditionalOnBean(WaitStrategy.class)
    public IRetryer DefaultRetryer(WaitStrategy waitStrategy) {
        return new DefaultRetryer(waitStrategy);
    }
}
