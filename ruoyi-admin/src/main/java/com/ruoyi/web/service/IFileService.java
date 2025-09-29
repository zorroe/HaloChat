package com.ruoyi.web.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传服务接口
 * 
 * @author ruoyi
 */
public interface IFileService
{
    /**
     * 上传用户头像
     *
     * @param file 头像文件
     * @param userId 用户ID
     * @return 头像访问URL
     */
    String uploadAvatar(MultipartFile file, String userId);

    /**
     * 删除头像文件
     *
     * @param avatarUrl 头像URL
     */
    void deleteAvatar(String avatarUrl);
}