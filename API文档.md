# HaloChat API 接口文档（中文版）

## 项目概述

HaloChat 是基于 RuoYi 框架开发的 Spring Boot 应用程序，提供用户认证、用户管理、头像管理等功能。

---

## 通用响应格式

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

---

## 1. 认证接口

### 1.1 用户登录

**请求方法：** `POST`

**URL 路径：** `/login`

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |

**示例请求：**
```json
{
  "username": "admin",
  "password": "123456"
}
```

**响应格式：**
- **成功响应：**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": null,
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| token | String | JWT 认证令牌 |

- **失败响应：**
```json
{
  "code": 500,
  "msg": "登录失败，请检查用户名和密码",
  "data": null
}
```

**错误码说明：**
- 500：登录失败，请检查用户名和密码

---

### 1.2 用户注册

**请求方法：** `POST`

**URL 路径：** `/register`

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |
| email | String | 是 | 邮箱 |
| nickName | String | 否 | 昵称 |

**示例请求：**
```json
{
  "username": "newuser",
  "password": "123456",
  "email": "user@example.com",
  "nickName": "新用户"
}
```

**响应格式：**
- **成功响应：**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": null
}
```

- **失败响应：**
```json
{
  "code": 500,
  "msg": "注册失败：用户名已存在",
  "data": null
}
```

**错误码说明：**
- 500：注册失败（如用户名已存在）

---

## 2. 用户管理接口

### 2.1 获取当前用户信息

**请求方法：** `GET`

**URL 路径：** `/user/getUser`

**请求参数：** 无

**认证要求：** 需要有效的 token

**响应格式：**
- **成功响应：**
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

| 字段 | 类型 | 说明 |
|------|------|------|
| userId | String | 用户ID |
| username | String | 用户名 |
| nickName | String | 昵称 |
| email | String | 邮箱 |
| avatar | String | 头像URL |
| createTime | String | 创建时间 |
| updateTime | String | 更新时间 |
| status | String | 用户状态（0-正常，1-停用） |

- **失败响应：**
```json
{
  "code": 500,
  "msg": "获取用户信息失败",
  "data": null
}
```

**错误码说明：**
- 500：获取用户信息失败

---

### 2.2 更新用户信息

**请求方法：** `POST`

**URL 路径：** `/user/update`

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | String | 否 | 用户ID（通常由系统自动获取） |
| nickName | String | 否 | 昵称 |
| email | String | 否 | 邮箱 |
| avatar | String | 否 | 头像URL |

**示例请求：**
```json
{
  "nickName": "新昵称",
  "email": "newemail@example.com",
  "avatar": "https://example.com/newavatar.jpg"
}
```

**响应格式：**
- **成功响应：**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": true
}
```

- **失败响应：**
```json
{
  "code": 500,
  "msg": "更新用户信息失败",
  "data": null
}
```

**错误码说明：**
- 500：更新用户信息失败

---

## 3. 头像管理接口

### 3.1 上传头像

**请求方法：** `POST`

**URL 路径：** `/avatar/upload`

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| avatar | File | 是 | 头像文件（图片格式） |

**认证要求：** 需要有效的 token

**请求示例：**
```
POST /avatar/upload
Content-Type: multipart/form-data

Form Data:
avatar: <file>
```

**响应格式：**
- **成功响应：**
```json
{
  "code": 200,
  "msg": "头像上传成功",
  "data": "https://example.com/uploads/avatar.jpg"
}
```

- **失败响应：**
```json
{
  "code": 400,
  "msg": "请选择要上传的头像文件",
  "data": null
}
```

```json
{
  "code": 401,
  "msg": "用户未登录",
  "data": null
}
```

```json
{
  "code": 500,
  "msg": "头像上传失败: <错误信息>",
  "data": null
}
```

**错误码说明：**
- 400：未选择头像文件
- 401：用户未登录
- 500：头像上传失败

---

### 3.2 获取当前用户头像

**请求方法：** `GET`

**URL 路径：** `/avatar/current`

**请求参数：** 无

**认证要求：** 需要有效的 token

**响应格式：**
- **成功响应：**
```json
{
  "code": 200,
  "msg": "获取头像成功",
  "data": "https://example.com/uploads/avatar.jpg"
}
```

- **失败响应：**
```json
{
  "code": 401,
  "msg": "用户未登录",
  "data": null
}
```

```json
{
  "code": 500,
  "msg": "获取头像失败: <错误信息>",
  "data": null
}
```

**错误码说明：**
- 401：用户未登录
- 500：获取头像失败

---

## 错误码汇总

| 错误码 | 说明 | 描述 |
|--------|------|------|
| 200 | 操作成功 | 请求成功执行 |
| 400 | 参数错误 | 请求参数不正确或缺失 |
| 401 | 未授权 | 用户未登录或令牌失效 |
| 500 | 服务器错误 | 服务器内部错误 |

---

## 认证机制

本 API 采用基于 Token 的认证机制。用户登录成功后会收到一个 JWT 令牌，在后续请求的 HTTP 头部中需要包含：

```
Authorization: Bearer <token>
```

或者在某些情况下，Token 可能通过其他方式传递，如请求参数或 Cookie。

---

## 注意事项

1. 所有日期时间格式统一为 "yyyy-MM-dd HH:mm:ss"
2. 文件上传接口需要使用 multipart/form-data 格式
3. 用户ID通常由系统自动获取，不需要在请求体中明确指定
4. 所有接口的响应数据格式统一，包含 code、msg、data 三个字段