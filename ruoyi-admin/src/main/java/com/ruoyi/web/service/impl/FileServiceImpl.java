package com.ruoyi.web.service.impl;

import cn.hutool.http.HttpStatus;
import com.ruoyi.common.config.MinioConfig;
import com.ruoyi.common.constant.FileConstants;
import com.ruoyi.common.exception.file.FileException;
import com.ruoyi.common.exception.file.InvalidExtensionException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileValidationUtils;
import com.ruoyi.common.utils.file.MimeTypeUtils;
import com.ruoyi.common.utils.file.MinioUtil;
import com.ruoyi.web.service.IFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

import static com.ruoyi.common.utils.file.MimeTypeUtils.getExtension;

/**
 * 文件上传服务实现
 *
 * @author ruoyi
 */
@Service
public class FileServiceImpl implements IFileService {
    @Autowired
    private MinioUtil minioUtil;

    @Autowired
    private MinioConfig minioConfig;

    /**
     * 上传用户头像
     *
     * @param file   头像文件
     * @param userId 用户ID
     * @return 头像访问URL
     */
    @Override
    public String uploadAvatar(MultipartFile file, String userId) {
        try {
            // 验证文件
            validateAvatarFile(file);

            // 构建存储对象名称 (例如: avatar/2025/09/29/user123_avatar.jpg)
            String objectName = buildAvatarObjectName(file, userId);

            // 上传到MinIO
            String avatarUrl = minioUtil.uploadFile(file, objectName);

            return avatarUrl;
        } catch (Exception e) {
            throw new FileException("upload.error", new Object[]{});
        }
    }

    /**
     * 删除头像文件
     *
     * @param avatarUrl 头像URL
     */
    @Override
    public void deleteAvatar(String avatarUrl) {
        if (StringUtils.isEmpty(avatarUrl)) {
            return;
        }

        try {
            // 从URL中提取对象名称
            String objectName = extractObjectNameFromUrl(avatarUrl);
            if (objectName != null) {
                minioUtil.deleteFile(objectName);
            }
        } catch (Exception e) {
            // 记录错误但不抛出异常，避免影响用户操作
            System.err.println("删除头像文件失败: " + e.getMessage());
        }
    }

    /**
     * 验证头像文件
     *
     * @param file 文件
     */
    private void validateAvatarFile(MultipartFile file) throws InvalidExtensionException {
        FileValidationUtils.validateAvatarFile(file);
    }

    /**
     * 构建头像对象名称
     *
     * @param file   上传的文件
     * @param userId 用户ID
     * @return 对象名称
     */
    private String buildAvatarObjectName(MultipartFile file, String userId) {
        String extension = getExtension(file.getOriginalFilename());
        String datePath = DateUtils.datePath(); // 格式: 2025/09/29
        String fileName = "user" + userId + "_avatar." + extension;

        return FileConstants.AVATAR_PATH_PREFIX + datePath + "/" + fileName;
    }

    /**
     * 从URL中提取对象名称
     *
     * @param url 完整URL
     * @return 对象名称
     */
    private String extractObjectNameFromUrl(String url) {
        if (url == null) {
            return null;
        }

        // 从URL中移除MinIO服务地址和桶名，获取对象路径
        String endpoint = minioConfig.getEndpoint();
        String bucketName = minioConfig.getBucketName();

        String prefix = endpoint + "/" + bucketName + "/";
        if (url.startsWith(prefix)) {
            return url.substring(prefix.length());
        }

        return null;
    }


}