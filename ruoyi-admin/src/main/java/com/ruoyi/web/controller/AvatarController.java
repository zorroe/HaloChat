package com.ruoyi.web.controller;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.web.service.IFileService;
import com.ruoyi.web.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户头像上传控制器
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/avatar")
public class AvatarController
{
    @Autowired
    private IFileService fileService;

    @Autowired
    private ISysUserService sysUserService;

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
            SysUser currentUser = sysUserService.getCurrentUser(request);
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
            
            boolean result = sysUserService.updateUser(updateUser, request);
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
            SysUser currentUser = sysUserService.getCurrentUser(request);
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

    /**
     * 删除用户头像
     */
    @DeleteMapping("/delete")
    public AjaxResult deleteAvatar(HttpServletRequest request)
    {
        try
        {
            // 获取当前用户
            SysUser currentUser = sysUserService.getCurrentUser(request);
            if (currentUser == null)
            {
                return AjaxResult.error("用户未登录");
            }

            // 如果用户当前有头像，则删除
            if (currentUser.getAvatar() != null && !currentUser.getAvatar().isEmpty())
            {
                fileService.deleteAvatar(currentUser.getAvatar());
            }

            // 将用户头像设置为空
            SysUser updateUser = new SysUser();
            updateUser.setUserId(currentUser.getUserId());
            updateUser.setAvatar(""); // 设置为空字符串
            
            boolean result = sysUserService.updateUser(updateUser, request);
            if (result)
            {
                return AjaxResult.success("头像删除成功");
            }
            else
            {
                return AjaxResult.error("更新用户信息失败");
            }
        }
        catch (Exception e)
        {
            return AjaxResult.error("删除头像失败: " + e.getMessage());
        }
    }
}