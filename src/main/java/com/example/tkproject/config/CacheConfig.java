package com.example.tkproject.config;

import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(RedissonClient redissonClient) {
        Map<String, org.redisson.spring.cache.CacheConfig> config = new HashMap<String, org.redisson.spring.cache.CacheConfig>();

        org.redisson.spring.cache.CacheConfig routesCacheConfig = new org.redisson.spring.cache.CacheConfig(60000, 300000);
        config.put("routesCache", routesCacheConfig);// TTL = 60000 ms, maxIdleTime = 300000 ms);

        org.redisson.spring.cache.CacheConfig locationsCacheConfig = new org.redisson.spring.cache.CacheConfig(60000, 300000);
        config.put("locationsCache", locationsCacheConfig);// TTL = 60000 ms, maxIdleTime = 300000 ms);

        org.redisson.spring.cache.CacheConfig transportationsCacheConfig = new org.redisson.spring.cache.CacheConfig(60000, 300000);
        config.put("transportationsCache", transportationsCacheConfig);// TTL = 60000 ms, maxIdleTime = 300000 ms);

        org.redisson.spring.cache.CacheConfig transportationCacheConfig = new org.redisson.spring.cache.CacheConfig(60000, 300000);
        config.put("transportationCache", transportationCacheConfig);// TTL = 60000 ms, maxIdleTime = 300000 ms);

        config.put("availableTransportsCache", new org.redisson.spring.cache.CacheConfig(60000, 300000));
        config.put("adjacencyListCache", new org.redisson.spring.cache.CacheConfig(60000, 300000));

        return new RedissonSpringCacheManager(redissonClient, config);
    }
}
