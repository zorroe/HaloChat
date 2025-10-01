package com.ruoyi.web.utils.file;

import com.ruoyi.web.config.MinioConfig;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * MinIO工具类，用于处理MinIO相关的辅助操作
 * 
 * @author zorroe
 */
@Component
public class MinioHelper {

    @Resource
    private MinioConfig minioConfig;

    /**
     * 从完整的MinIO URL中提取对象名称
     * 
     * @param url 完整URL
     * @return 对象名称，如果无法提取则返回null
     */
    public String extractObjectNameFromUrl(String url) {
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

    /**
     * 验证URL是否为有效的MinIO URL
     * 
     * @param url URL
     * @return 是否有效
     */
    public boolean isValidMinioUrl(String url) {
        if (url == null) {
            return false;
        }

        String endpoint = minioConfig.getEndpoint();
        String bucketName = minioConfig.getBucketName();

        String prefix = endpoint + "/" + bucketName + "/";
        return url.startsWith(prefix);
    }
}