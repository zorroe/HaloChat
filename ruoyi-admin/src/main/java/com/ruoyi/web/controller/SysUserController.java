package com.ruoyi.web.controller;

import com.ruoyi.web.domain.AjaxResult;
import com.ruoyi.web.domain.entity.SysUser;
import com.ruoyi.web.domain.model.LoginUser;
import com.ruoyi.web.service.IFileService;
import com.ruoyi.web.service.ISysUserService;
import com.ruoyi.web.service.TokenService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * UserController
 */
@RequestMapping("/user")
@RestController
public class SysUserController {

    @Resource
    private ISysUserService userService;

    @Resource
    private IFileService fileService;

    @Resource
    private TokenService tokenService;

    /**
     * 获取当前用户信息
     *
     * @return 当前用户信息
     */
    @GetMapping("/getUser")
    public AjaxResult getUser(HttpServletRequest request) {
        return AjaxResult.success(userService.getCurrentUser(request));
    }

    /**
     * 获取用户头像临时访问URL
     * 
     * @param avatarUrl 用户头像原始URL
     * @param expires 过期时间（秒），可选，默认1小时
     * @param request HTTP请求
     * @return 临时访问URL
     */
    @GetMapping("/getAvatarTempUrl")
    public AjaxResult getAvatarTempUrl(
            @RequestParam String avatarUrl,
            @RequestParam(required = false, defaultValue = "3600") Integer expires,
            HttpServletRequest request) {
        
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (loginUser == null) {
            return AjaxResult.error("用户未登录");
        }
        
        try {
            String tempUrl = userService.getUserAvatarTempUrl(avatarUrl, expires);
            return AjaxResult.success(tempUrl);
        } catch (Exception e) {
            return AjaxResult.error("获取临时访问URL失败: " + e.getMessage());
        }
    }

    /**
     * 修改用户信息
     */
    @PostMapping("/update")
    public AjaxResult update(@RequestBody SysUser sysUser, HttpServletRequest request) {
        return AjaxResult.success(userService.updateUser(sysUser, request));
    }
}
