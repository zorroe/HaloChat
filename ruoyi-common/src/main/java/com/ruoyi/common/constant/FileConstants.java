package com.ruoyi.common.constant;

/**
 * 文件相关常量
 * 
 * @author ruoyi
 */
public class FileConstants
{
    /**
     * 头像文件最大大小 (5MB)
     */
    public static final long AVATAR_MAX_SIZE = 5 * 1024 * 1024;

    /**
     * 头像允许的文件扩展名
     */
    public static final String[] AVATAR_ALLOWED_EXTENSION = {
        "bmp", "gif", "jpg", "jpeg", "png"
    };

    /**
     * 默认头像URL
     */
    public static final String DEFAULT_AVATAR = "/profile/avatar/default.png";

    /**
     * 头像存储路径前缀
     */
    public static final String AVATAR_PATH_PREFIX = "avatar/";
}