package com.dail.starter.cache.controller;

import com.dail.starter.cache.service.CacheTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
