package com.ruoyi.web.service;

import com.ruoyi.web.domain.entity.SysUser;
import com.ruoyi.web.exception.user.UserPasswordNotMatchException;
import com.ruoyi.web.utils.SecurityUtils;
import com.ruoyi.web.security.context.AuthenticationContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * 登录密码方法
 * 
 * @author ruoyi
 */
@Component
public class SysPasswordService
{

    public void validate(SysUser user)
    {
        Authentication usernamePasswordAuthenticationToken = AuthenticationContextHolder.getContext();
        String password = usernamePasswordAuthenticationToken.getCredentials().toString();

        if (!matches(user, password))
        {
            throw new UserPasswordNotMatchException();
        }
    }

    public boolean matches(SysUser user, String rawPassword)
    {
        return SecurityUtils.matchesPassword(rawPassword, user.getPassword());
    }
}
