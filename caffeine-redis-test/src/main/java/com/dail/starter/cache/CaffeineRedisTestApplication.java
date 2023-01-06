package com.dail.starter.cache;

import com.dail.starter.cache.autoconfigure.EnableCache;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * description 启动类
 *
 * @author Dail 2023/01/04 08:58
 */
@SpringBootApplication
@EnableCache
@EnableAsync
public class CaffeineRedisTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(CaffeineRedisTestApplication.class, args);
    }

}
