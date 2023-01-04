## caffeine + redis做一级、二级缓存
### 一、cache-starter缓存组件
cache-starter中默认**Caffeine**作为一级缓存、使用 Redis 作为二级缓存，支持分布式缓存清除。 

#### 1.配置类
- **MultiCacheAutoConfiguration**: 多级缓存注入配置类
- **ProcessHandlerConfiguration**: 数据处理器配置类,配置缓存清除消费端
- **RedisQueueConfiguration**: Redis消息队列配置类,监听缓存清除消息
- **RetryAutoConfiguration**: 消息发布重试配置类
#### 2.缓存清除广播端
redis消息发布进行对分布式应用进行缓存清除通知，利用服务名作为消息通道
~~~java
    /**
     * 向通道发送消息
     *
     * @param channel  消息通道
     * @param message  消息
     * @param tryTimes 重试次数
     */
    public void sendChannelMessage(String channel, String message, Integer tryTimes) {
        // 如未配置重试器，则直接发送渠道消息
        if (retryer == null) {
            this.sendChannelMessage(channel, message);
        } else {

            Retryer<?> retry = retryer.buildRetry(tryTimes);
            try {
                retry.call(() -> {
                    this.sendChannelMessage(channel, message);
                    return null;
                });
            } catch (Exception e) {
                // 出现异常则直接处理
                this.sendChannelMessage(channel, message);
            }
        }
    }
~~~
默认重试策略配置
~~~yaml
cache:
  retry:
    # 重试策略 fixed-固定时间 increment-递增 random-随机 fibonacci-斐波那契数列(max-time 数列位置) exponential-指数(2的重试次数次方*基础时间)
    strategy: ${RETRY_STRATEGY:increment}
    # 基础时间(秒)
    base-time: ${RETRY_BASE_TIME:0}
    # 最大时间(秒)
    max-time: ${RETRY_MAX_TIME:3}
~~~
~~~java
public Retryer<Void> buildRetry(Integer retryTimes) {
        retryTimes = retryTimes == null ? 0 : retryTimes;
        return RetryerBuilder.<Void>newBuilder()
                // 如果异常会重试
                .retryIfException()
                // 重调策略
                .withWaitStrategy(waitStrategy)
                // 尝试次数
                .withStopStrategy(StopStrategies.stopAfterAttempt(retryTimes + 1))
                .build();
}
~~~

#### 3.缓存清除监听端
redis消息监听来进行一二级缓存清除，支持集群、分布式应用缓存清除。
~~~java
    /**
     * 监听消费
     *
     * @param body 消息内容
     */
    public void receiveMessage(String body) {
        Message<?> message = JSON.parseObject(body, Message.class);
        rmqMsgHandler.doMessage(message);
    }
~~~
### 二、caffeine-redis-test测试应用服务
#### 1.引用cache-starter缓存组件
~~~xml
        <dependency>
            <groupId>com.dail</groupId>
            <artifactId>cache-starter</artifactId>
            <version>0.0.1-SNAPSHOT</version>
        </dependency>
~~~
#### 2.配置文件
配置服务名、端口、redis配置、缓存配置
~~~yaml
spring:
  application:
    name: cache-test
  redis:
    host: 127.0.0.1
    port: 6379
    connect-timeout: 2000
    timeout: 5000
    lettuce:
      pool:
        max-active: 50
        max-idle: 50
        max-wait: 5000
    database: 2
  cache:
    multi:
      caches:
        cache-test: 
          l1-spec: initialCapacity=100,maximumSize=5000,expireAfterWrite=3600s
          l2-spec: expiration=0
      l1:
        enabled: true
      l2:
        enabled: true
server:
  port: 7777

cache:
  retry:
    strategy: increment
    base-time: 0
    max-time: 3
~~~
#### 3.使用缓存
~~~java
@Autowired
private MultiCacheManager multiCacheManager;

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
~~~
#### 4.清除缓存
分布式根据服务名来进行清除
~~~java
@Value("${spring.application.name:cache-test}")
private String applicationName;
@Autowired
private RedisPublisher redisPublisher;

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
~~~
#### 5.缓存清除
- 接收服务端配置: **CacheTestProcessHandlerConfiguration**，缓存消费处理配置类
- **CacheTestProcessHandler**：缓存清除测试消息处理器
### TODO
