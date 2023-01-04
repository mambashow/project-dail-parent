package com.dail.starter.cache.config;

import com.dail.starter.cache.mq.redis.RedisReceiver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/**
 * description Redis消息队列配置类
 *
 * @author Dail 2023/01/03 17:05
 */
@Configuration
public class RedisQueueConfiguration {

    @Value("${spring.application.name:cache-test}")
    private String applicationName;

    /**
     * 构建redis消息监听器容器
     *
     * @param connectionFactory      连接工厂
     * @param defaultListenerAdapter 监听
     * @return redis消息监听容器
     */
    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                                   MessageListenerAdapter defaultListenerAdapter,
                                                   MessageListenerAdapter generalListenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        // 指定方法监听不同的频道,可同时监听多个渠道，这里订阅服务名的渠道
        container.addMessageListener(defaultListenerAdapter, new PatternTopic(applicationName));
        return container;
    }

    /**
     * 默认系统消息适配器
     *
     * @param receiver 消息接收器
     * @return 消息监听
     */
    @Bean(name = "defaultListenerAdapter")
    public MessageListenerAdapter defaultListenerAdapter(RedisReceiver receiver) {
        // 指定类中回调接收消息的方法
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }
}
