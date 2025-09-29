package com.ruoyi.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * MinIO配置类
 * 
 * @author ruoyi
 */
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

    public String getEndpoint()
    {
        return endpoint;
    }

    public void setEndpoint(String endpoint)
    {
        this.endpoint = endpoint;
    }

    public String getAccessKey()
    {
        return accessKey;
    }

    public void setAccessKey(String accessKey)
    {
        this.accessKey = accessKey;
    }

    public String getSecretKey()
    {
        return secretKey;
    }

    public void setSecretKey(String secretKey)
    {
        this.secretKey = secretKey;
    }

    public String getBucketName()
    {
        return bucketName;
    }

    public void setBucketName(String bucketName)
    {
        this.bucketName = bucketName;
    }
}