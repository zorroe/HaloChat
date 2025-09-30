package com.ruoyi.web.controller;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.web.service.IFileService;
import com.ruoyi.web.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
     * 修改用户信息
     */
    @PostMapping("/update")
    public AjaxResult update(@RequestBody SysUser sysUser, HttpServletRequest request) {
        return AjaxResult.success(userService.updateUser(sysUser, request));
    }
}
