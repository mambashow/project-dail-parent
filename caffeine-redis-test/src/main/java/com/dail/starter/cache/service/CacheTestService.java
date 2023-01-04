package com.dail.starter.cache.service;

/**
 * description 缓存测试接口
 *
 * @author Dail 2023/01/03 16:32
 */
public interface CacheTestService {

    /**
     * 测试
     *
     * @param userId 用户id
     * @return 数据
     */
    String test(Long userId);

    /**
     * 分布式刷新缓存测试
     *
     * @param userId 用户id
     * @return 数据
     */
    String refreshTest(Long userId);

}
