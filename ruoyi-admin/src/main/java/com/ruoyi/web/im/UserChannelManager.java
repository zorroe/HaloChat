package com.ruoyi.web.im;

import io.netty.channel.Channel;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserChannelManager {

    // 核心映射表：userId -> Channel（ConcurrentHashMap保证线程安全）
    private final Map<Long, Channel> userChannelMap = new ConcurrentHashMap<>();

    /**
     * 用户上线：关联userId和Channel
     */
    public void addUserChannel(Long userId, Channel channel) {
        // 若用户已在其他端登录，可选择关闭旧连接（踢下线）或允许多端登录
        Channel oldChannel = userChannelMap.put(userId, channel);
        if (oldChannel != null && oldChannel != channel && oldChannel.isActive()) {
            oldChannel.close(); // 踢掉旧连接（可选，根据业务需求）
        }
    }

    /**
     * 用户下线：移除userId和Channel的关联
     */
    public void removeUserChannel(Long userId) {
        userChannelMap.remove(userId);
    }

    /**
     * 根据userId获取Channel
     */
    public Channel getChannel(Long userId) {
        return userChannelMap.get(userId);
    }

    /**
     * 判断用户是否在线
     * @param userId 用户ID
     * @return true-在线（Channel存在且活跃），false-离线
     */
    public boolean isOnline(Long userId) {
        Channel channel = userChannelMap.get(userId);
        // 关键：Channel必须存在且活跃（isActive()为true）
        return channel != null && channel.isActive();
    }

}
