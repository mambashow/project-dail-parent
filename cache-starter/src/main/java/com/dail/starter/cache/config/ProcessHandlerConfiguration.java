package com.dail.starter.cache.config;

import com.dail.starter.cache.mq.processer.DefaultRmqMsgHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description 数据处理器配置类
 *
 * @author Dail 2023/01/04 08:34
 */
@Configuration
public class ProcessHandlerConfiguration {


    //================================Begin MQ消息处理器================================

    @Bean
    @ConditionalOnMissingBean
    public DefaultRmqMsgHandler defaultRmqMsgHandler() {
        return new DefaultRmqMsgHandler();
    }


    //================================End MQ消息处理器================================
}
