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
 * userController
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

    @PostMapping("/update")
    public AjaxResult update(@RequestBody SysUser sysUser, HttpServletRequest request) {
        return AjaxResult.success(userService.updateUser(sysUser, request));
    }

    /**
     * 上传用户头像
     */
    @PostMapping("/upload")
    public AjaxResult uploadAvatar(@RequestParam("avatar") MultipartFile file, HttpServletRequest request)
    {
        try
        {
            if (file.isEmpty())
            {
                return AjaxResult.error("请选择要上传的头像文件");
            }

            // 获取当前用户
            SysUser currentUser = userService.getCurrentUser(request);
            if (currentUser == null)
            {
                return AjaxResult.error("用户未登录");
            }

            // 上传头像到MinIO
            String avatarUrl = fileService.uploadAvatar(file, currentUser.getUserId());

            // 更新用户头像信息到数据库
            SysUser updateUser = new SysUser();
            updateUser.setUserId(currentUser.getUserId());
            updateUser.setAvatar(avatarUrl);

            boolean result = userService.updateUser(updateUser, request);
            if (result)
            {
                return AjaxResult.success("头像上传成功", avatarUrl);
            }
            else
            {
                return AjaxResult.error("更新用户信息失败");
            }
        }
        catch (Exception e)
        {
            return AjaxResult.error("头像上传失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前用户头像
     */
    @GetMapping("/current")
    public AjaxResult getCurrentAvatar(HttpServletRequest request)
    {
        try
        {
            // 获取当前用户
            SysUser currentUser = userService.getCurrentUser(request);
            if (currentUser == null)
            {
                return AjaxResult.error("用户未登录");
            }

            return AjaxResult.success("获取头像成功", currentUser.getAvatar());
        }
        catch (Exception e)
        {
            return AjaxResult.error("获取头像失败: " + e.getMessage());
        }
    }
}
