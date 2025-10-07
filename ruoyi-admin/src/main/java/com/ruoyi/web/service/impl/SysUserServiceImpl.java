package com.ruoyi.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.web.config.MinioConfig;
import com.ruoyi.web.constant.FileConstants;
import com.ruoyi.web.domain.entity.SysUser;
import com.ruoyi.web.domain.model.LoginUser;
import com.ruoyi.web.mapper.SysUserMapper;
import com.ruoyi.web.service.ITempFileService;
import com.ruoyi.web.service.ISysUserService;
import com.ruoyi.web.service.TokenService;
import com.ruoyi.web.utils.file.MinioHelper;
import com.ruoyi.web.utils.file.MinioUtil;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Resource
    private ITempFileService tempFileService;

    @Override
    public boolean checkUserNameUnique(SysUser sysUser) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserName, sysUser.getUserName());
        return count(queryWrapper) == 0;
    }

    @Override
    public SysUser getCurrentUser(HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        SysUser user = loginUser.getUser();
        
        // 如果用户头像存在，生成临时访问URL
        if (user != null && user.getAvatar() != null) {
            try {
                // 提取对象名称并生成临时URL
                String objectName = extractObjectNameFromUrl(user.getAvatar());
                if (objectName != null && tempFileService.validateAvatarPath(objectName)) {
                    String tempAvatarUrl = tempFileService.generateAvatarTempUrl(objectName, 3600); // 1小时过期
                    user.setAvatar(tempAvatarUrl);
                }
            } catch (Exception e) {
                // 如果生成临时URL失败，保持原始URL
                // 日志记录但不抛出异常
            }
        }
        
        return user;
    }

    @Override
    public boolean updateUser(SysUser sysUser, HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        SysUser user = loginUser.getUser();
        user.setNickName(sysUser.getNickName());
        user.setPhone(sysUser.getPhone());
        user.setEmail(sysUser.getEmail());
        user.setGender(sysUser.getGender());
        // 更新头像信息（如果提供了新头像）
        if (sysUser.getAvatar() != null) {
            user.setAvatar(sysUser.getAvatar());
        }
        loginUser.setUser(user);
        tokenService.setLoginUser(loginUser);
        return updateById(user);
    }

    @Override
    public String getUserAvatarTempUrl(String avatarUrl, int expires) {
        try {
            // 从原始URL中提取对象名称
            String objectName = extractObjectNameFromUrl(avatarUrl);
            if (objectName != null && tempFileService.validateAvatarPath(objectName)) {
                return tempFileService.generateAvatarTempUrl(objectName, expires);
            }
            return avatarUrl; // 如果无法提取对象名，则返回原始URL
        } catch (Exception e) {
            // 如果生成临时URL失败，返回原始URL
            return avatarUrl;
        }
    }

    @Resource
    private MinioHelper minioHelper;

    /**
     * 从URL中提取对象名称
     * 
     * @param url 完整URL
     * @return 对象名称
     */
    private String extractObjectNameFromUrl(String url) {
        return minioHelper.extractObjectNameFromUrl(url);
    }
}
