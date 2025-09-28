package com.ruoyi.common.constant;

/**
 * 用户常量信息
 * 
 * @author ruoyi
 */
public class UserConstants
{
    /** 是否为系统默认（是） */
    public static final String YES = "Y";

    /** 校验是否唯一的返回标识 */
    public final static boolean UNIQUE = true;
    public final static boolean NOT_UNIQUE = false;

    /**
     * 用户名长度限制
     */
    public static final int USERNAME_MIN_LENGTH = 2;
    public static final int USERNAME_MAX_LENGTH = 20;

    /**
     * 密码长度限制
     */
    public static final int PASSWORD_MIN_LENGTH = 5;
    public static final int PASSWORD_MAX_LENGTH = 20;
}
