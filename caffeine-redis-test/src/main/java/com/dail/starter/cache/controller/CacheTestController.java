package com.dail.starter.cache.controller;

import com.dail.starter.cache.dao.UserInfo;
import com.dail.starter.cache.service.CacheTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * description 缓存测试接口controller入口
 *
 * @author Dail 2023/01/03 16:18
 */
@RestController
public class CacheTestController {

    @Autowired
    private CacheTestService cacheTestService;

    /**
     * 缓存测试
     *
     * @param userId 用户id
     * @return 数据
     */
    @RequestMapping(value = "/test/{userId}")
    public String queryUser(@PathVariable Long userId) {
        return cacheTestService.test(userId);
    }

    /**
     * 分布式刷新缓存（主要删除缓存）
     *
     * @param userId 用户id
     * @return 数据
     */
    @RequestMapping(value = "/refresh/{userId}")
    public String refreshTest(@PathVariable Long userId) {
        return cacheTestService.refreshTest(userId);
    }

    @RequestMapping(value = "/add-user",method = RequestMethod.POST)
    public List<UserInfo> addUser(@RequestBody List<UserInfo> userInfoList) {
        return cacheTestService.addUser(userInfoList);
    }

    @RequestMapping(value = "/query-user",method = RequestMethod.POST)
    public List<UserInfo> queryUser(@RequestBody List<Integer> userIdList) {
        return cacheTestService.queryUser(userIdList);
    }
}
