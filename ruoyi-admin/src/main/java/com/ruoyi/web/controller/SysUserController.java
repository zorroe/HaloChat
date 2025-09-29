package com.ruoyi.web.controller;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.web.service.ISysUserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * userController
 */
@RequestMapping("/user")
@RestController
public class SysUserController {

    @Resource
    private ISysUserService userService;


    /**
     * 获取当前用户信息
     *
     * @return 当前用户信息
     */
    @GetMapping("/getUser")
    public AjaxResult getUser(HttpServletRequest request) {
        return AjaxResult.success(userService.getCurrentUser(request));
    }

    @PostMapping("/update")
    public AjaxResult update(@RequestBody SysUser sysUser, HttpServletRequest request) {
        return AjaxResult.success(userService.updateUser(sysUser, request));
    }
}
