package com.ruoyi.web.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson配置类
 * 
 * @author zorroe
 */
@Configuration
public class RedissonConfig {

    @Value("${spring.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.redis.port:6379}")
    private int redisPort;

    @Value("${spring.redis.password:}")
    private String redisPassword;

    @Value("${spring.redis.database:0}")
    private int redisDatabase;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        String redisAddress = "redis://" + redisHost + ":" + redisPort;
        
        if (redisPassword != null && !redisPassword.trim().isEmpty()) {
            config.useSingleServer()
                    .setAddress(redisAddress)
                    .setPassword(redisPassword)
                    .setDatabase(redisDatabase);
        } else {
            config.useSingleServer()
                    .setAddress(redisAddress)
                    .setDatabase(redisDatabase);
        }

        return Redisson.create(config);
    }
}