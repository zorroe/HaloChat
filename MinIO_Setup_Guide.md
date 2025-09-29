# MinIO 服务器配置指南

## 1. MinIO 简介

MinIO 是一个高性能的对象存储服务，兼容 Amazon S3 API。它非常适合存储大文件，如图片、视频等。

## 2. MinIO 安装

### 2.1 使用 Docker 安装（推荐）
```bash
docker run -p 9000:9000 -p 9001:9001 --name minio \
  -e "MINIO_ROOT_USER=minioadmin" \
  -e "MINIO_ROOT_PASSWORD=minioadmin" \
  -v /mnt/data:/data minio/minio server /data --console-address ":9001"
```

### 2.2 使用二进制文件安装
1. 下载 MinIO 服务器二进制文件
   - Linux: `wget https://dl.min.io/server/minio/release/linux-amd64/minio`
   - Windows: `wget https://dl.min.io/server/minio/release/windows-amd64/minio.exe`

2. 设置权限并启动
   ```bash
   chmod +x minio
   ./minio server /data --console-address ":9001"
   ```

## 3. MinIO 控制台配置

访问 `http://localhost:9001` 进入 MinIO 控制台，使用以下默认凭据：
- 用户名: `minioadmin`
- 密码: `minioadmin`

## 4. 存储桶创建

1. 登录 MinIO 控制台
2. 点击 "Buckets" 菜单
3. 点击 "Create Bucket" 按钮
4. 输入桶名称（如 `avatar-bucket`）
5. 设置访问策略（建议设置为私有）
6. 点击 "Create Bucket"

## 5. 访问策略配置

为头像存储桶配置适当的访问策略，确保安全性：

```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Principal": {
                "AWS": [
                    "*"
                ]
            },
            "Action": [
                "s3:GetObject"
            ],
            "Resource": [
                "arn:aws:s3:::avatar-bucket/*"
            ]
        }
    ]
}
```

## 6. RuoYi 应用配置

在 `application.yml` 中配置 MinIO 连接参数：

```yaml
# MinIO配置
minio:
  # MinIO服务地址 (请根据实际部署地址修改)
  endpoint: http://localhost:9000
  # 访问密钥 (与MinIO服务器配置一致)
  accessKey: minioadmin
  # 私有密钥 (与MinIO服务器配置一致)
  secretKey: minioadmin
  # 存储桶名称 (在MinIO服务器上创建的桶名)
  bucketName: avatar-bucket
```

## 7. 安全建议

### 7.1 认证安全
- 生产环境请使用强密码替换默认的 `minioadmin` 凭据
- 定期更换访问密钥和私有密钥
- 使用专用的访问密钥，避免使用管理员密钥

### 7.2 网络安全
- 使用 SSL/TLS 加密传输
- 在服务器前配置反向代理（如 Nginx）
- 限制对 MinIO 服务器的网络访问

### 7.3 数据安全
- 定期备份 MinIO 数据目录
- 配置适当的访问控制策略
- 使用对象版本控制防止意外删除

## 8. 性能优化

### 8.1 服务器配置
- 确保服务器有足够内存和 CPU
- 使用 SSD 存储以获得更好的 I/O 性能
- 配置合适的网络带宽

### 8.2 客户端优化
- 调整连接池大小
- 启用压缩传输
- 使用合适的分片大小

## 9. 监控与维护

### 9.1 日志监控
- 启用访问日志记录
- 监控错误日志
- 定期检查系统性能

### 9.2 定期维护
- 清理过期文件
- 检查磁盘空间
- 更新 MinIO 版本

## 10. 故障排除

### 10.1 常见问题
- 连接被拒绝：检查 MinIO 服务是否运行
- 认证失败：验证 accessKey 和 secretKey 是否正确
- 无法访问存储桶：确认存储桶名称和访问策略

### 10.2 日志查看
```bash
# Docker 容器日志
docker logs minio

# 检查进程状态
docker ps | grep minio
```

## 11. 生产环境部署

生产环境建议使用分布式 MinIO 部署，以获得更高的可用性和性能：

```bash
docker run -d --name minio \
  -p 9000:9000 -p 9001:9001 \
  -e "MINIO_ROOT_USER=YOUR_ACCESS_KEY" \
  -e "MINIO_ROOT_PASSWORD=YOUR_SECRET_KEY" \
  -v /path/to/data:/data \
  minio/minio server /data --console-address ":9001"
```

### 11.1 反向代理配置 (Nginx 示例)
```nginx
upstream minio_servers {
    server localhost:9000;
}

server {
    listen 80;
    server_name minio.yourdomain.com;

    location / {
        proxy_pass http://minio_servers;
        proxy_set_header Host $http_host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_buffering off;
        proxy_request_buffering off;
    }
}
```

## 12. 备份与恢复

### 12.1 数据备份
定期备份 MinIO 数据目录：
```bash
# 备份数据目录
tar -czf minio-backup-$(date +%Y%m%d).tar.gz /path/to/minio/data
```

### 12.2 配置备份
备份 MinIO 配置和用户信息：
```bash
# 备份配置目录
docker cp minio:/root/.minio/ minio-config-backup/
```

## 13. 升级指南

在升级 MinIO 时：

1. 停止 MinIO 服务
2. 备份数据和配置
3. 拉取最新镜像或下载新版本
4. 启动新版本服务
5. 验证数据完整性
6. 测试应用连接

## 14. 参考资源

- [MinIO 官方文档](https://docs.min.io/)
- [MinIO Java SDK](https://docs.min.io/docs/java-client-quickstart-guide.html)
- [S3 API 兼容性](https://docs.aws.amazon.com/AmazonS3/latest/API/Welcome.html)