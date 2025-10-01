package com.ruoyi.web.service.impl;

import com.ruoyi.web.config.MinioConfig;
import com.ruoyi.web.constant.FileConstants;
import com.ruoyi.web.service.ITempFileService;
import com.ruoyi.web.utils.file.MinioUtil;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 临时文件访问服务实现
 * 
 * @author zorroe
 */
@Service
public class TempFileServiceImpl implements ITempFileService {

    @Resource
    private MinioUtil minioUtil;

    @Resource
    private MinioConfig minioConfig;

    /**
     * 生成头像临时访问URL
     * 
     * @param objectName 文件对象名称
     * @param expires 过期时间（秒）
     * @return 临时访问URL
     */
    @Override
    public String generateAvatarTempUrl(String objectName, int expires) {
        if (!validateAvatarPath(objectName)) {
            throw new IllegalArgumentException("非法的头像路径");
        }

        try {
            MinioClient client = minioUtil.getMinioClient();
            
            // 如果未指定过期时间，默认为1小时
            if (expires <= 0) {
                expires = 3600; // 1小时
            }
            
            // 生成预签名URL
            return client.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(minioConfig.getBucketName())
                    .object(objectName)
                    .expiry(expires, TimeUnit.SECONDS)
                    .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("生成临时访问URL失败", e);
        }
    }

    /**
     * 验证头像文件路径是否合法
     * 
     * @param objectName 文件对象名称
     * @return 是否合法
     */
    @Override
    public boolean validateAvatarPath(String objectName) {
        if (objectName == null || objectName.trim().isEmpty()) {
            return false;
        }
        
        // 检查路径是否以头像路径前缀开始，防止路径穿越
        return objectName.startsWith(FileConstants.AVATAR_PATH_PREFIX);
    }
}