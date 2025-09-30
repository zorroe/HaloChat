package com.ruoyi.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.web.mapper.SysUserMapper;
import com.ruoyi.web.service.ISysUserService;
import com.ruoyi.web.service.TokenService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author zorroe
 * @since 2025-09-28
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    @Resource
    private TokenService tokenService;

    @Override
    public boolean checkUserNameUnique(SysUser sysUser) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserName, sysUser.getUserName());
        return count(queryWrapper) == 0;
    }

    @Override
    public SysUser getCurrentUser(HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        return loginUser.getUser();
    }

    @Override
    public boolean updateUser(SysUser sysUser, HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        SysUser user = loginUser.getUser();
        user.setNickName(sysUser.getNickName());
        user.setPhonenumber(sysUser.getPhonenumber());
        user.setEmail(sysUser.getEmail());
        user.setSex(sysUser.getSex());
        // 更新头像信息（如果提供了新头像）
        if (sysUser.getAvatar() != null) {
            user.setAvatar(sysUser.getAvatar());
        }
        loginUser.setUser(user);
        tokenService.setLoginUser(loginUser);
        return updateById(user);
    }
}
