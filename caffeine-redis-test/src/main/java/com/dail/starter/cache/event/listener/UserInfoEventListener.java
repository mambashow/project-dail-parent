package com.dail.starter.cache.event.listener;

import com.alibaba.fastjson.JSON;
import com.dail.starter.cache.dao.UserInfo;
import com.dail.starter.cache.event.UserInfoEvent;
import com.dail.starter.cache.multi.MultiCacheManager;
import com.dail.starter.cache.utils.BaseConstants;
import com.dail.starter.cache.utils.BaseObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * description 用户信息缓存更改事件监听
 *
 * @author Dail 2023/01/06 14:27
 */
@Component
public class UserInfoEventListener implements ApplicationListener<UserInfoEvent> {

    public static final Logger LOGGER = LoggerFactory.getLogger(UserInfoEventListener.class);

    @Autowired
    private MultiCacheManager multiCacheManager;

    @Override
    @Async
    public void onApplicationEvent(UserInfoEvent event) {
        LOGGER.info("缓存事件监听处理,当前线程:{}", Thread.currentThread().getName());
        Cache cache = multiCacheManager.getCache(BaseConstants.CacheName.CACHE_TEST_NAME);
        List<UserInfo> userInfos = BaseObjectUtil.castList(event.getSource(), UserInfo.class);
        for (UserInfo userInfo : userInfos) {
            String key = String.format(BaseConstants.CacheKeyTemplate.CACHE_TEST, userInfo.getUserId());
            if (cache != null) {
                cache.put(key, JSON.toJSONString(userInfo));
            }
        }
    }
}
