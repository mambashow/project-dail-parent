package com.dail.starter.cache.service.impl;

import com.alibaba.fastjson.JSON;
import com.dail.starter.cache.mq.redis.Message;
import com.dail.starter.cache.mq.redis.RedisPublisher;
import com.dail.starter.cache.multi.MultiCacheManager;
import com.dail.starter.cache.service.CacheTestService;
import com.dail.starter.cache.utils.BaseConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * description 缓存测试接口实现
 *
 * @author Dail 2023/01/03 16:32
 */
@Service
@Slf4j
public class CacheTestServiceImpl implements CacheTestService {

    @Autowired
    private MultiCacheManager multiCacheManager;

    @Autowired
    private RedisPublisher redisPublisher;

    @Value("${spring.application.name:cache-test}")
    private String applicationName;

    /**
     * 重试次数，默认3次
     */
    public static final Integer defaultRetryTimes = 3;

    @Override
    public String test(Long userId) {
        Cache cache = multiCacheManager.getCache(BaseConstants.CacheName.CACHE_TEST_NAME);
        String key = String.format(BaseConstants.CacheKeyTemplate.CACHE_TEST, userId);
        assert cache != null;
        String s = cache.get(key, String.class);
        if (StringUtils.isBlank(s)) {
            String userInfo = "{\"userId\":" + userId + ",\"userName\":\"test" + userId + "\"}";
            cache.put(key, userInfo);
            s = userInfo;
        }
        log.info("数据:{}", s);
        return s;
    }

    @Override
    public String refreshTest(Long userId) {
        Message<String> message = new Message<>();
        message.setCommand(BaseConstants.RmqCommand.REFRESH_TEST_CACHE)
                .setBody(JSON.toJSONString(userId));
        // 设置防重复处理的KEY
        message.setReplayKey(getReplayKey());
        String body = JSON.toJSONString(message);
        try {
            redisPublisher.sendChannelMessage(applicationName, body, defaultRetryTimes);
        } catch (Exception e) {
            // 重试发送有异常，则尝试单次发送
            redisPublisher.sendChannelMessage(applicationName, body);
        }
        return "Y";
    }

    /**
     * 获取防重复处理的KEY
     *
     * @return KEY
     */
    private String getReplayKey() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
