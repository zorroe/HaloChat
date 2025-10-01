package com.ruoyi.web.utils.file;


import com.ruoyi.web.config.MinioConfig;
import io.minio.*;
import io.minio.messages.Bucket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.InputStream;
import java.util.List;

/**
 * MinIO 文件上传服务类
 * 
 * @author ruoyi
 */
@Component
public class MinioUtil
{
    private static final Logger log = LoggerFactory.getLogger(MinioUtil.class);

    @Resource
    private MinioConfig minioConfig;

    private MinioClient minioClient;

    @PostConstruct
    public void init()
    {
        try
        {
            // 初始化MinIO客户端
            minioClient = MinioClient.builder()
                    .endpoint(minioConfig.getEndpoint())
                    .credentials(minioConfig.getAccessKey(), minioConfig.getSecretKey())
                    .build();

            // 检查存储桶是否存在，不存在则创建
            boolean bucketExists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(minioConfig.getBucketName()).build());
            if (!bucketExists)
            {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioConfig.getBucketName()).build());
                log.info("创建MinIO存储桶: {}", minioConfig.getBucketName());
            }
            else
            {
                log.info("MinIO存储桶已存在: {}", minioConfig.getBucketName());
            }
        }
        catch (Exception e)
        {
            log.error("初始化MinIO客户端失败", e);
            throw new RuntimeException("初始化MinIO客户端失败", e);
        }
    }

    /**
     * 上传文件到MinIO
     *
     * @param file 上传的文件
     * @param objectName 存储对象名称
     * @return 文件访问URL
     */
    public String uploadFile(MultipartFile file, String objectName)
    {
        try
        {
            // 检查文件是否为空
            if (file.isEmpty())
            {
                throw new Exception("上传的文件不能为空");
            }

            // 获取文件输入流
            try (InputStream inputStream = file.getInputStream())
            {
                // 上传文件到MinIO
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(minioConfig.getBucketName())
                                .object(objectName)
                                .stream(inputStream, file.getSize(), -1)
                                .contentType(file.getContentType())
                                .build()
                );

                // 返回文件访问URL
                return generateFileUrl(objectName);
            }
        }
        catch (Exception e)
        {
            log.error("上传文件失败", e);
            throw new RuntimeException("上传文件失败", e);
        }
    }

    /**
     * 上传文件到MinIO（带输入流）
     *
     * @param inputStream 输入流
     * @param objectName 存储对象名称
     * @param size 文件大小
     * @param contentType 内容类型
     * @return 文件访问URL
     */
    public String uploadFile(InputStream inputStream, String objectName, long size, String contentType)
    {
        try
        {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(objectName)
                            .stream(inputStream, size, -1)
                            .contentType(contentType)
                            .build()
            );

            return generateFileUrl(objectName);
        }
        catch (Exception e)
        {
            log.error("上传文件失败", e);
            throw new RuntimeException("上传文件失败", e);
        }
    }

    /**
     * 删除MinIO中的文件
     *
     * @param objectName 存储对象名称
     */
    public void deleteFile(String objectName)
    {
        try
        {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(objectName)
                            .build()
            );
        }
        catch (Exception e)
        {
            log.error("删除文件失败", e);
            throw new RuntimeException("删除文件失败", e);
        }
    }

    /**
     * 检查文件是否存在
     *
     * @param objectName 存储对象名称
     * @return 存在返回true，否则返回false
     */
    public boolean fileExists(String objectName)
    {
        try
        {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(objectName)
                            .build()
            );
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    /**
     * 生成文件访问URL
     *
     * @param objectName 存储对象名称
     * @return 文件访问URL
     */
    public String generateFileUrl(String objectName)
    {
        // 构建文件访问URL
        return objectName;
    }

    /**
     * 列出所有存储桶
     */
    public List<Bucket> listBuckets()
    {
        try
        {
            return minioClient.listBuckets();
        }
        catch (Exception e)
        {
            log.error("列出存储桶失败", e);
            throw new RuntimeException("列出存储桶失败", e);
        }
    }

    /**
     * 获取MinIO客户端实例（用于其他操作）
     */
    public MinioClient getMinioClient()
    {
        return minioClient;
    }
}