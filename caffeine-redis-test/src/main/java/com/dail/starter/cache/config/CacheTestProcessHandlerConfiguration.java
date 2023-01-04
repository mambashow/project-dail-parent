package com.dail.starter.cache.config;

import com.dail.starter.cache.processer.command.handler.CacheTestProcessHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description 缓存消费处理配置类
 *
 * @author Dail 2023/01/04 10:28
 */
@Configuration
public class CacheTestProcessHandlerConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CacheTestProcessHandler cacheProcessHandler() {
        return new CacheTestProcessHandler();
    }
}
