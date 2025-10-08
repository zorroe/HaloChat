# HaloChat

HaloChat是一个现代化的实时聊天应用程序，采用前后端分离架构，后端使用Spring Boot框架，前端使用Vue.js框架。该应用提供用户认证、用户管理、头像管理等功能，支持实时通信和用户信息管理。

## 项目概述

HaloChat 是一款基于 RuoYi 框架开发的 Spring Boot 应用程序，提供用户认证、用户管理、头像管理等功能。该系统采用现代化技术栈，前后端分离设计，为用户提供流畅的聊天体验。

主要特点：
- **后端 (HaloChat)**：使用 Spring Boot、Spring Security、Redis 和 JWT 认证
- **前端 (HaloChatApp)**：使用 Vue 3、TypeScript 和 Vite 构建现代前端应用
- **权限管理**：基于 JWT 的安全认证机制，支持多终端认证系统
- **高效开发**：使用代码生成器可以一键生成前后端代码

## 技术栈

### 后端技术栈 (HaloChat)
- **核心框架**：Spring Boot 2.5.15
- **安全框架**：Spring Security
- **认证机制**：JWT (JSON Web Token)
- **数据库**：PostgreSQL，使用 MyBatis Plus 作为 ORM 框架
- **缓存**：Redis，使用 Redisson 实现分布式锁
- **数据源**：阿里巴巴 Druid 连接池
- **开发语言**：Java 8+
- **构建工具**：Maven
- **API 文档**：Swagger 3
- **系统信息**：oshi-core 获取系统信息
- **JSON 处理**：Fastjson2

### 前端技术栈 (HaloChatApp)
- **前端框架**：Vue 3，使用 Composition API
- **编程语言**：TypeScript
- **构建工具**：Vite
- **HTTP 客户端**：Axios
- **类型定义**：Node.js 类型定义

## 功能特性

- 用户认证系统（登录/注册）
- 实时聊天功能
- 用户资料管理
- 头像上传与管理
- 基于 JWT 的安全认证
- 角色权限控制
- 响应式设计，支持多设备访问
- 完善的 API 文档

## 安装指南

### 系统要求

- **后端**：
  - Java 8 或更高版本
  - Maven 3.6+
  - PostgreSQL 数据库
  - Redis 服务器

- **前端**：
  - Node.js 16+ 或更高版本
  - pnpm 包管理器

### 后端安装 (HaloChat)

1. 克隆项目仓库：
   ```bash
   git clone https://github.com/your-username/HaloChat.git
   cd HaloChat
   ```

2. 配置数据库连接，在 `application.yml` 文件中修改：
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/halochat
       username: your_db_username
       password: your_db_password
   ```

3. 初始化数据库，运行 `sql` 目录下的 SQL 脚本

4. 构建项目：
   ```bash
   mvn clean install
   ```

5. 启动应用程序：
   ```bash
   mvn spring-boot:run
   ```
   
   或打包后运行 JAR 文件：
   ```bash
   mvn package
   java -jar ruoyi-admin/target/ruoyi.jar
   ```

6. 后端服务器将在 `http://localhost:8080` 运行

### 前端安装 (HaloChatApp)

1. 进入前端目录：
   ```bash
   cd ../HaloChatApp
   ```

2. 安装依赖：
   ```bash
   pnpm install
   ```

3. 启动开发服务器：
   ```bash
   pnpm dev
   ```

4. 前端应用将在 `http://localhost:5173` 访问

## API 文档

后端提供全面的 REST API 接口用于用户管理和聊天功能：

### 通用响应格式
所有 API 接口返回统一格式的 JSON 数据：
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {}
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| code | Integer | 状态码，200 表示成功 |
| msg | String | 响应消息 |
| data | Object | 响应数据体（可能为 null 或对象） |

### 1. 认证接口

#### 1.1 用户登录
- **请求方法**：`POST`
- **URL 路径**：`/login`
- **请求参数**：
  - `username` (String, 必填): 用户名
  - `password` (String, 必填): 密码
- **示例请求**：
  ```json
  {
    "username": "admin",
    "password": "123456"
  }
  ```
- **响应示例**：
  ```json
  {
    "code": 200,
    "msg": "操作成功",
    "data": null,
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  }
  ```

#### 1.2 用户注册
- **请求方法**：`POST`
- **URL 路径**：`/register`
- **请求参数**：
  - `username` (String, 必填): 用户名
  - `password` (String, 必填): 密码
  - `email` (String, 必填): 邮箱
  - `nickName` (String, 可选): 昵称
- **示例请求**：
  ```json
  {
    "username": "newuser",
    "password": "123456",
    "email": "user@example.com",
    "nickName": "新用户"
  }
  ```

### 2. 用户管理接口

#### 2.1 获取当前用户信息
- **请求方法**：`GET`
- **URL 路径**：`/user/getUser`
- **认证要求**：需要有效的 token
- **响应示例**：
  ```json
  {
    "code": 200,
    "msg": "操作成功",
    "data": {
      "userId": "1",
      "username": "admin",
      "nickName": "管理员",
      "email": "admin@example.com",
      "avatar": "https://example.com/avatar.jpg",
      "createTime": "2023-01-01 12:00:00",
      "updateTime": "2023-01-01 12:00:00",
      "status": "0"
    }
  }
  ```

#### 2.2 更新用户信息
- **请求方法**：`POST`
- **URL 路径**：`/user/update`
- **认证要求**：需要有效的 token
- **请求参数**：
  - `nickName` (String, 可选): 昵称
  - `email` (String, 可选): 邮箱
  - `avatar` (String, 可选): 头像URL
- **示例请求**：
  ```json
  {
    "nickName": "新昵称",
    "email": "newemail@example.com",
    "avatar": "https://example.com/newavatar.jpg"
  }
  ```

### 3. 头像管理接口

#### 3.1 上传头像
- **请求方法**：`POST`
- **URL 路径**：`/avatar/upload`
- **认证要求**：需要有效的 token
- **请求格式**：multipart/form-data
- **请求参数**：
  - `avatar` (File, 必填): 头像文件（图片格式）
- **响应示例**：
  ```json
  {
    "code": 200,
    "msg": "头像上传成功",
    "data": "https://example.com/uploads/avatar.jpg"
  }
  ```

#### 3.2 获取当前用户头像
- **请求方法**：`GET`
- **URL 路径**：`/avatar/current`
- **认证要求**：需要有效的 token
- **响应示例**：
  ```json
  {
    "code": 200,
    "msg": "获取头像成功",
    "data": "https://example.com/uploads/avatar.jpg"
  }
  ```

### 认证机制
本 API 采用基于 Token 的认证机制。用户登录成功后会收到一个 JWT 令牌，在后续请求的 HTTP 头部中需要包含：
```
Authorization: Bearer <token>
```

## 环境变量配置

### 后端配置

在 `application.yml` 文件中配置以下环境特定设置：

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/halochat
    username: ${DB_USERNAME:halochat}
    password: ${DB_PASSWORD:halochat}
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD:}
  servlet:
    multipart:
      max-file-size: 5MB
      max-request-size: 10MB

jwt:
  secret: ${JWT_SECRET:defaultSecretKey}
  expiration: 86400000 # 24小时
```

### 前端配置

在前端目录中创建 `.env` 文件：

```env
VITE_API_BASE_URL=http://localhost:8080
VITE_WS_URL=ws://localhost:8080/chat
```

## 开发脚本

### 后端脚本

- `mvn clean install` - 构建项目
- `mvn spring-boot:run` - 在开发模式下运行应用
- `mvn test` - 运行单元测试

### 前端脚本

- `pnpm dev` - 启动开发服务器
- `pnpm build` - 构建生产版本
- `pnpm preview` - 预览生产构建

## 项目结构

```
HaloChat/
├── ruoyi-admin/              # 后端 Spring Boot 应用
│   ├── src/
│   │   └── main/
│   │       ├── java/         # Java 源代码
│   │       └── resources/    # 配置文件
│   └── pom.xml               # Maven 依赖配置
├── HaloChatApp/              # 前端 Vue.js 应用
│   ├── src/                  # 源代码
│   ├── public/               # 静态资源
│   ├── package.json          # Node.js 依赖配置
│   └── vite.config.ts        # Vite 配置
├── sql/                      # 数据库初始化脚本
├── API文档.md                # API 文档
└── README.md                 # 项目文档
```

## 贡献指南

我们欢迎各种形式的贡献！以下是参与项目开发的方式：

1. Fork 项目仓库
2. 创建功能分支 (`git checkout -b feature/新功能`)
3. 进行代码修改
4. 提交修改 (`git commit -m '添加新功能'`)
5. 推送到分支 (`git push origin feature/新功能`)
6. 创建 Pull Request

### 开发规范

- 遵循现有的代码风格和命名规范
- 为新功能和错误修复编写测试
- 按需更新相关文档
- 保持 Pull Request 专注于单一功能或修复
- 提交 Pull Request 前确保所有测试通过

### 本地开发设置

1. 克隆两个仓库：
   ```bash
   git clone <后端仓库地址> HaloChat
   git clone <前端仓库地址> HaloChatApp
   ```

2. 按照安装说明设置后端和前端环境

3. 本地运行两个应用并测试集成

### 代码审查流程

1. 确保提交的代码符合编码标准
2. 验证所有测试用例通过
3. 添加适当的注释和文档
4. 代码审查通过后合并到主分支

## 许可证信息

本项目采用 MIT 许可证。详情请参见 [LICENSE](LICENSE) 文件。

Copyright (c) 2018 RuoYi

特此免费授予任何获得本软件及相关文档文件（以下简称"软件"）副本的人无限制地处理本软件的权限，包括但不限于使用、复制、修改、合并、发布、分发、再许可和/或销售软件副本的权限，以及允许软件所有者提供此等服务的权限，但须符合以下条件：

上述版权声明和本许可声明应包含在软件的所有副本或实质部分中。

本软件按"原样"提供，不提供任何形式的明示或暗示担保，包括但不限于适销性、特定用途适用性和非侵权性的担保。在任何情况下，作者或版权持有人均不对因软件或软件的使用或其他交易而引起的任何索赔、损害或其他责任承担责任，无论是合同诉讼、侵权行为还是其他诉讼。

后端组件基于 RuoYi 框架开发，该框架有其自身的许可条款。

---

## 支持

如果您在使用过程中遇到问题或有关于项目的问题，请在 GitHub 仓库中提交 issue。

## 致谢

- 基于 RuoYi 框架进行快速开发
- Vue.js 和 Spring Boot 提供了强大的框架支持
- 开源社区提供的各类库和工具