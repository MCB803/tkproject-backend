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

        config.put("routesCache", new org.redisson.spring.cache.CacheConfig(60000, 300000));
        config.put("locationsCache", new org.redisson.spring.cache.CacheConfig(60000, 300000));
        config.put("transportationsCache", new org.redisson.spring.cache.CacheConfig(60000, 300000));
        config.put("availableTransportsCache", new org.redisson.spring.cache.CacheConfig(60000, 300000));
        config.put("adjacencyListCache", new org.redisson.spring.cache.CacheConfig(60000, 300000));

        return new RedissonSpringCacheManager(redissonClient, config);
    }
}
