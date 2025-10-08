package com.ruoyi.web.im;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 维护用户与Channel的绑定关系
 */
public class ChannelRegistry {
    private static final Map<String, Channel> USER_CHANNELS = new ConcurrentHashMap<>();

    public static void bind(String userId, Channel channel) {
        USER_CHANNELS.put(userId, channel);
    }

    public static void unbind(String userId) {
        USER_CHANNELS.remove(userId);
    }

    public static Channel get(String userId) {
        return USER_CHANNELS.get(userId);
    }
}
