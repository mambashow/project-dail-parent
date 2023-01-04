
package com.dail.starter.cache.config;

import com.dail.starter.cache.l1.caffeine.CaffeineL1CacheManager;
import com.dail.starter.cache.l2.L2CacheManager;
import com.dail.starter.cache.l2.redis.RedisL2CacheManager;
import com.dail.starter.cache.multi.MultiCacheManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * description 多级缓存自动化配置类
 *
 * @author Dail 2023/01/03 15:20
 */
@Configuration
@EnableConfigurationProperties({MultiCacheProperties.class, RedisProperties.class})
public class MultiCacheAutoConfiguration {

    @Bean
    @Primary
    public MultiCacheManager multiCacheManager(RedisConnectionFactory connectionFactory,
                                               MultiCacheProperties multiCacheProperties) {
        CaffeineL1CacheManager l1CacheManager = null;
        if (multiCacheProperties.getL1().isEnabled()) {
            if (CaffeineL1CacheManager.type().equals(multiCacheProperties.getL1().getType())) {
                l1CacheManager = new CaffeineL1CacheManager();
                l1CacheManager.setAllowNullValues(multiCacheProperties.isAllowNullValues());
            }
        }
        L2CacheManager l2CacheManager = null;
        if (multiCacheProperties.getL2().isEnabled()) {
            if (RedisL2CacheManager.type().equals(multiCacheProperties.getL2().getType())) {
                l2CacheManager = new RedisL2CacheManager(connectionFactory);
            }
        }
        return new MultiCacheManager(l1CacheManager, l2CacheManager, multiCacheProperties);
    }

    @Bean
    @ConditionalOnMissingBean(RedisTemplate.class)
    public RedisTemplate<Object, Object> redisTemplate(
            RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean
    @ConditionalOnMissingBean(StringRedisTemplate.class)
    public StringRedisTemplate stringRedisTemplate(
            RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

}

