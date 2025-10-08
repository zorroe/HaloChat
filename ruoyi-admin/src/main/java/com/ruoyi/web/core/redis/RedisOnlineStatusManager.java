package com.ruoyi.web.core.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 分布式在线状态管理（基于Redis）
 */
@Component
public class RedisOnlineStatusManager {

    private final RedisTemplate<String, String> redisTemplate;
    // Redis Key：存储在线用户，key=online_users，value=userId（或Hash结构存储userId->实例信息）
    private static final String ONLINE_USER_KEY = "online_users";

    public RedisOnlineStatusManager(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 标记用户在线
     */
    public void markOnline(Long userId) {
        // 用Redis的Set存储在线用户ID（自动去重）
        redisTemplate.opsForSet().add(ONLINE_USER_KEY, userId.toString());
        // 可选：设置过期时间（配合心跳续期，防止服务宕机后状态残留）
        redisTemplate.expire(ONLINE_USER_KEY, 30, TimeUnit.MINUTES);
    }

    /**
     * 标记用户离线
     */
    public void markOffline(Long userId) {
        redisTemplate.opsForSet().remove(ONLINE_USER_KEY, userId.toString());
    }

    /**
     * 分布式场景下查询用户是否在线（跨实例）
     */
    public boolean isOnlineDistributed(Long userId) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(ONLINE_USER_KEY, userId.toString()));
    }

    /**
     * 心跳续期（防止在线状态过期）
     */
    public void renewOnlineStatus(Long userId) {
        markOnline(userId); // 重新添加并续期
    }
}