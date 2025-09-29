# MinIO 集成与用户头像上传功能实现文档

## 1. 概述

本文档详细说明了如何在 RuoYi 系统中集成 MinIO 对象存储服务，并实现用户头像上传功能。

## 2. 集成的组件

### 2.1 MinIO 配置 (`MinioConfig.java`)
- 用于管理 MinIO 服务的配置信息
- 支持 endpoint、accessKey、secretKey、bucketName 配置

### 2.2 MinIO 工具类 (`MinioUtil.java`)
- 提供 MinIO 客户端初始化和服务管理
- 实现文件上传、删除、查询等基础功能
- 包含最佳实践，如自动创建存储桶

### 2.3 文件服务接口 (`IFileService.java`)
- 定义文件操作接口，包括头像上传和删除
- 可扩展支持其他文件类型

### 2.4 文件服务实现 (`FileServiceImpl.java`)
- 实现头像上传的业务逻辑
- 包含文件验证和安全检查
- 集成 MinIOUtil 进行文件操作

### 2.5 头像控制器 (`AvatarController.java`)
- 提供头像上传、获取、删除的 REST API
- 集成用户认证和权限验证
- 统一返回格式

## 3. 配置文件

### 3.1 application.yml 配置
```yaml
# MinIO配置
minio:
  # MinIO服务地址 (例如: http://localhost:9000)
  endpoint: http://localhost:9000
  # 访问密钥
  accessKey: minioadmin
  # 私有密钥
  secretKey: minioadmin
  # 存储桶名称
  bucketName: avatar-bucket
```

### 3.2 Maven 依赖配置
在主 pom.xml 和 ruoyi-admin 模块中添加了 MinIO 相关依赖。

## 4. API 接口

### 4.1 上传头像
- **URL**: `/avatar/upload`
- **方法**: `POST`
- **参数**: `avatar` (MultipartFile)
- **返回**: 
  ```json
  {
    "code": 200,
    "msg": "头像上传成功",
    "data": "http://localhost:9000/avatar-bucket/avatar/2025/09/29/user1_avatar.jpg"
  }
  ```

### 4.2 获取当前用户头像
- **URL**: `/avatar/current`
- **方法**: `GET`
- **返回**:
  ```json
  {
    "code": 200,
    "msg": "获取头像成功",
    "data": "http://localhost:9000/avatar-bucket/avatar/2025/09/29/user1_avatar.jpg"
  }
  ```

### 4.3 删除头像
- **URL**: `/avatar/delete`
- **方法**: `DELETE`
- **返回**:
  ```json
  {
    "code": 200,
    "msg": "头像删除成功",
    "data": null
  }
  ```

## 5. 数据库变更

系统使用现有的 `sys_user` 表中的 `avatar` 字段存储头像 URL，无需数据库变更。

## 6. 安全措施

- 文件大小限制（最大 5MB）
- 文件类型验证（仅允许图片格式）
- 用户身份验证（确保用户只能上传自己的头像）
- 自动清理旧头像文件

## 7. 部署说明

### 7.1 MinIO 服务器准备
1. 安装并启动 MinIO 服务器
2. 创建名为 `avatar-bucket` 的存储桶（或在配置中自定义）
3. 配置访问权限

### 7.2 应用配置
1. 更新 `application.yml` 中的 MinIO 连接配置
2. 确保网络连接正常
3. 启动应用服务器

## 8. 最佳实践

- 使用日期路径组织文件：`avatar/2025/09/29/user1_avatar.jpg`
- 自动验证文件类型和大小
- 上传成功后立即更新用户信息
- 删除旧头像文件以节省空间
- 使用 HTTPS 确保传输安全

## 9. 错误处理

- 文件过大：返回 "头像文件大小不能超过5MB"
- 文件类型不正确：返回 "不支持的文件类型"
- 用户未登录：返回 "用户未登录"
- 上传失败：返回具体错误信息

## 10. 扩展性

该实现具有良好的扩展性：
- 可轻松添加其他文件类型的上传功能
- 支持多个存储桶
- 可配置不同的文件策略
- 支持分布式部署