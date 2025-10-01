package com.ruoyi.web.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * MinIO配置类
 * 
 * @author ruoyi
 */
@Data
@Component
@ConfigurationProperties(prefix = "minio")
public class MinioConfig
{
    /** 服务地址 */
    private String endpoint;

    /** 访问密钥 */
    private String accessKey;

    /** 私有密钥 */
    private String secretKey;

    /** 存储桶名称 */
    private String bucketName;

    /** 临时访问URL过期时间（秒） */
    private Integer tempExpireTime;
}