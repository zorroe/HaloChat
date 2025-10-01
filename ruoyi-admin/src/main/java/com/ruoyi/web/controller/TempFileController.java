package com.ruoyi.web.controller;

import com.ruoyi.web.config.MinioConfig;
import com.ruoyi.web.domain.AjaxResult;
import com.ruoyi.web.domain.model.LoginUser;
import com.ruoyi.web.service.ITempFileService;
import com.ruoyi.web.service.TokenService;
import com.ruoyi.web.utils.file.MinioUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 临时文件访问控制器
 * 提供头像等文件的临时访问凭证
 *
 * @author zorroe
 */
@RestController
@RequestMapping("/temp-file")
public class TempFileController {

    @Resource
    private ITempFileService tempFileService;

    @Resource
    private TokenService tokenService;

    @Resource
    private MinioConfig minioConfig;

    /**
     * 获取用户头像临时访问URL（需要用户身份验证）
     *
     * @param objectName 文件对象名称
     * @param request    HTTP请求
     * @return 临时访问URL
     */
    @GetMapping("/user-avatar-temp-url")
    public AjaxResult getUserAvatarTempUrl(
            @RequestParam String objectName,
            HttpServletRequest request) {

        // 验证用户身份
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (loginUser == null) {
            return AjaxResult.error("用户未登录");
        }

        // 验证路径合法性
        if (!tempFileService.validateAvatarPath(objectName)) {
            return AjaxResult.error("非法的头像路径");
        }

        // 生成临时访问URL
        try {
            String tempUrl = tempFileService.generateAvatarTempUrl(objectName, minioConfig.getTempExpireTime());
            return AjaxResult.success(tempUrl);
        } catch (Exception e) {
            return AjaxResult.error("生成临时访问URL失败: " + e.getMessage());
        }
    }
}