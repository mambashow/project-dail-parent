package com.dail.starter.cache.service.impl;

import com.alibaba.fastjson.JSON;
import com.dail.starter.cache.dao.UserInfo;
import com.dail.starter.cache.event.UserInfoEvent;
import com.dail.starter.cache.mapper.UserInfoMapper;
import com.dail.starter.cache.mq.redis.Message;
import com.dail.starter.cache.mq.redis.RedisPublisher;
import com.dail.starter.cache.multi.MultiCacheManager;
import com.dail.starter.cache.service.CacheTestService;
import com.dail.starter.cache.utils.BaseConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
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

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    /**
     * 重试次数，默认3次
     */
    public static final Integer DEFAULT_RETRY_TIMES = 3;

    @Override
    public String test(Long userId) {
        Cache cache = multiCacheManager.getCache(BaseConstants.CacheName.CACHE_TEST_NAME);
        String key = String.format(BaseConstants.CacheKeyTemplate.CACHE_TEST, userId);
        assert cache != null;
        String s = cache.get(key, String.class);
        if (StringUtils.isBlank(s)) {
            UserInfo info = new UserInfo().setUserId(Math.toIntExact(userId));
            List<UserInfo> userInfos = userInfoMapper.queryByUserInfo(info);
            if (CollectionUtils.isNotEmpty(userInfos)) {
                for (UserInfo userInfo : userInfos) {
                    cache.put(key, userInfo);
                }
                s = JSON.toJSONString(userInfos.get(0));
            }
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
            redisPublisher.sendChannelMessage(applicationName, body, DEFAULT_RETRY_TIMES);
        } catch (Exception e) {
            // 重试发送有异常，则尝试单次发送
            redisPublisher.sendChannelMessage(applicationName, body);
        }
        return "Y";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<UserInfo> addUser(List<UserInfo> userInfoList) {
        log.info("更新或者新增user,当前线程:{}", Thread.currentThread().getName());
        for (UserInfo userInfo : userInfoList) {
            UserInfo info = new UserInfo().setUserId(userInfo.getUserId()).setUserName(userInfo.getUserName());
            List<UserInfo> userInfos = userInfoMapper.queryByUserInfo(info);
            if (CollectionUtils.isNotEmpty(userInfos)) {
                userInfoMapper.updateUser(info);
            } else {
                userInfoMapper.addUser(userInfo);
            }
        }
        eventPublisher.publishEvent(new UserInfoEvent(userInfoList));
        return userInfoList;
    }

    @Override
    public List<UserInfo> queryUser(List<Integer> userIdList) {
        List<UserInfo> userInfos = new ArrayList<>();
        for (Integer integer : userIdList) {
            List<UserInfo> userInfos1 = userInfoMapper.queryByUserInfo(new UserInfo().setUserId(integer));
            if (CollectionUtils.isNotEmpty(userInfos1)) {
                userInfos.addAll(userInfos1);
            }
        }
        return userInfos;
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
