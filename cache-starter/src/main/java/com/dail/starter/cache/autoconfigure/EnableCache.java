package com.dail.starter.cache.autoconfigure;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * description 缓存组件服务注解
 *
 * @author Dail 2023/01/04 08:43
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({CacheAutoConfiguration.class})
public @interface EnableCache {


}
