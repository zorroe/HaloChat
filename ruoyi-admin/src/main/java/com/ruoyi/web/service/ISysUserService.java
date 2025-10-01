package com.ruoyi.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.web.domain.entity.SysUser;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 用户信息表 服务类
 * </p>
 *
 * @author zorroe
 * @since 2025-09-28
 */
public interface ISysUserService extends IService<SysUser> {

    boolean checkUserNameUnique(SysUser sysUser);

    SysUser getCurrentUser(HttpServletRequest request);

    boolean updateUser(SysUser sysUser, HttpServletRequest request);
    
    /**
     * 获取用户头像临时访问URL
     * 
     * @param avatarUrl 用户头像URL
     * @param expires 过期时间（秒）
     * @return 临时访问URL
     */
    String getUserAvatarTempUrl(String avatarUrl, int expires);
}
