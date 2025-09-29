package com.ruoyi.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.domain.entity.SysUser;

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
}
