package com.dail.starter.cache.utils;

import com.alibaba.fastjson.JSON;
import com.dail.starter.cache.dao.UserInfo;
import com.dail.starter.cache.mapper.UserInfoMapper;
import com.dail.starter.cache.multi.MultiCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * description 初始化用户数据
 *
 * @author Dail 2023/01/06 14:08
 */
@Component
public class CacheTestInit implements InitializingBean {

    public static final Logger logger = LoggerFactory.getLogger(CacheTestInit.class);

    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private MultiCacheManager multiCacheManager;

    @Override
    public void afterPropertiesSet() throws Exception {
        List<UserInfo> userInfos = userInfoMapper.queryByUserInfo(new UserInfo());
        Cache cache = multiCacheManager.getCache(BaseConstants.CacheName.CACHE_TEST_NAME);
        int count = 0;
        if (cache != null) {
            for (UserInfo userInfo : userInfos) {
                String format = String.format(BaseConstants.CacheKeyTemplate.CACHE_TEST, userInfo.getUserId());
                cache.put(format, JSON.toJSONString(userInfo));
                count++;
            }
        }
        logger.info("用户信息缓存初始化完成，处理数量:{}", count);
    }
}
