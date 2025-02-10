package com.example.tkproject.config;

import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(RedissonClient redissonClient) {
        // Define cache configuration for your caches. For example, for a cache named "routes":
        Map<String, org.redisson.spring.cache.CacheConfig> config = Map.of(
                "routes", new org.redisson.spring.cache.CacheConfig(60000, 300000) // TTL = 60000 ms, maxIdleTime = 300000 ms
        );
        return new RedissonSpringCacheManager(redissonClient, config);
    }
}
