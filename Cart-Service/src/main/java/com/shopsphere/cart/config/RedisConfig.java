package com.shopsphere.cart.config;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
public class RedisConfig {

    private static final Logger log =
            LoggerFactory.getLogger(RedisConfig.class);

    @Bean
    @Primary
    CacheManager cacheManager(ObjectProvider<RedisConnectionFactory> provider) {

        RedisConnectionFactory factory = provider.getIfAvailable();

        if (factory != null && isRedisUp(factory)) {

            log.info("Redis is UP → Using RedisCacheManager");

            return RedisCacheManager.builder(factory)
                    .cacheDefaults(redisConfig())
                    .build();
        }

        log.warn("Redis is DOWN → Using InMemory Cache");

        return new ConcurrentMapCacheManager("cart");
    }

    private boolean isRedisUp(RedisConnectionFactory factory) {
        try {
            factory.getConnection().ping();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private RedisCacheConfiguration redisConfig() {

        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues();
    }

    @Bean
    CacheErrorHandler cacheErrorHandler() {

        return new CacheErrorHandler() {

            @Override
            public void handleCacheGetError(RuntimeException e, Cache cache, Object key) {
                log.warn("Redis GET failed | cache={} key={}", cache.getName(), key);
            }

            @Override
            public void handleCachePutError(RuntimeException e, Cache cache, Object key, Object value) {
                log.warn("Redis PUT failed | cache={} key={}", cache.getName(), key);
            }

            @Override
            public void handleCacheEvictError(RuntimeException e, Cache cache, Object key) {
                log.warn("Redis EVICT failed | cache={} key={}", cache.getName(), key);
            }

            @Override
            public void handleCacheClearError(RuntimeException e, Cache cache) {
                log.warn("Redis CLEAR failed | cache={}", cache.getName());
            }
        };
    }
}