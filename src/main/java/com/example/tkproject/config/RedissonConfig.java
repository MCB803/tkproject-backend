package com.example.tkproject.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class RedissonConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Bean
    public RedissonClient redissonClient() {
        // Build the Redis URI (e.g., redis://localhost:6379)
        String redisUri = "redis://" + redisHost + ":" + redisPort;
        Config config = new Config();
        config.useSingleServer().setAddress(redisUri);
        config.setCodec(new JsonJacksonCodec());
        return Redisson.create(config);
    }

    @Bean(name = "asyncExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);       // Adjust core pool size as needed
        executor.setMaxPoolSize(50);          // Adjust maximum pool size as needed
        executor.setQueueCapacity(100);       // Adjust queue capacity as needed
        executor.setThreadNamePrefix("AsyncRoute-");
        executor.initialize();
        // Wrap the executor to propagate the SecurityContext to async threads.
        return new DelegatingSecurityContextExecutor(executor);
    }
}
