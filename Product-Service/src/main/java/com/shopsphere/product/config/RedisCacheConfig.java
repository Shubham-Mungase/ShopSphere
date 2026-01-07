package com.shopsphere.product.config;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class RedisCacheConfig {

    private static final Logger log = LoggerFactory.getLogger(RedisCacheConfig.class);

    @Bean
    @Primary
     CacheManager cacheManager(
            ObjectProvider<RedisConnectionFactory> redisFactoryProvider) {

        RedisConnectionFactory factory = redisFactoryProvider.getIfAvailable();

        if (factory != null && isRedisUp(factory)) {
            log.info("Redis is UP → Using RedisCacheManager");
            return redisCacheManager(factory);
        }

        log.warn("Redis is DOWN → Falling back to In-Memory Cache");
        return inMemoryCacheManager();
    }

    private boolean isRedisUp(RedisConnectionFactory factory) {
        try {
            factory.getConnection().ping();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private CacheManager redisCacheManager(RedisConnectionFactory factory) {

        Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();

        cacheConfigs.put("productSearch", redisConfig(60));
        cacheConfigs.put("categoryTree", redisConfig(60));
        cacheConfigs.put("categoryById", redisConfig(60));
        cacheConfigs.put("categories", redisConfig(60));
        cacheConfigs.put("productById", redisConfig(60));
        cacheConfigs.put("productsByCategory", redisConfig(60));
        cacheConfigs.put("productImageById", redisConfig(60));
        cacheConfigs.put("productImagesByProduct", redisConfig(60));

        return RedisCacheManager.builder(factory)
                .withInitialCacheConfigurations(cacheConfigs)
                .build();
    }

    private RedisCacheConfiguration redisConfig(long ttlMinutes) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(ttlMinutes))
                .disableCachingNullValues()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new JacksonJsonRedisSerializer<>(Object.class)));
    }

    private CacheManager inMemoryCacheManager() {
        return new ConcurrentMapCacheManager(
                "productSearch",
                "categoryTree",
                "categoryById",
                "categories",
                "productById",
                "productsByCategory",
                "productImageById",
                "productImagesByProduct"
        );
    }
    
    @Bean
    CacheErrorHandler cacheErrorHandler() {
        return new CacheErrorHandler() {

            @Override
            public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
                // log Redis GET errors
                log.warn("Redis CACHE-GET failed | cache={} | key={} | reason={}", 
                         cache.getName(), key, exception.getMessage());
            }

            @Override
            public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
                log.warn("Redis CACHE-PUT failed | cache={} | key={} | reason={}", 
                         cache.getName(), key, exception.getMessage());
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
                log.warn("Redis CACHE-EVICT failed | cache={} | key={} | reason={}", 
                         cache.getName(), key, exception.getMessage());
            }

            @Override
            public void handleCacheClearError(RuntimeException exception, Cache cache) {
                log.warn("Redis CACHE-CLEAR failed | cache={} | reason={}", 
                         cache.getName(), exception.getMessage());
            }
        };
    }
}
