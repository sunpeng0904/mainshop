---
name: api-design
description: "当需要设计RESTful API接口、制定接口规范、设计请求响应格式、生成API文档时使用"
---

# API设计

## Overview

设计规范的API接口，确保接口易用、安全、可维护。遵循RESTful规范，统一请求响应格式。

## When to Use

- 需要设计RESTful API接口
- 需要制定接口规范
- 需要设计请求响应格式
- 需要生成API文档

## Core Pattern

| 设计要素 | 说明 | 规范 |
|----------|------|------|
| URL设计 | 资源路径 | 名词复数、小写、连字符 |
| HTTP方法 | 操作类型 | GET/POST/PUT/PATCH/DELETE |
| 请求格式 | 请求参数 | JSON格式 |
| 响应格式 | 返回结果 | 统一结构 |
| 错误码 | 错误标识 | 标准HTTP状态码 |

## Implementation

1. **接口识别** - 识别业务接口、确定接口粒度、设计接口分组
2. **接口设计** - 设计请求格式、响应格式、错误码
3. **安全设计** - 设计认证方式、权限控制、限流策略
4. **文档生成** - 生成API文档、Mock数据、SDK

## Templates

### API设计规范

```markdown
# API设计规范

## 1. URL设计
### 1.1 基本规则
- 使用名词复数：/users, /orders
- 使用小写字母
- 使用连字符分隔：/user-profiles
- 避免层级过深：/users/{id}/orders

### 1.2 版本控制
- URL路径：/api/v1/users
- 请求头：Accept: application/vnd.api.v1+json

## 2. HTTP方法
| 方法 | 用途 | 示例 |
|------|------|------|
| GET | 查询 | GET /users |
| POST | 创建 | POST /users |
| PUT | 全量更新 | PUT /users/{id} |
| PATCH | 部分更新 | PATCH /users/{id} |
| DELETE | 删除 | DELETE /users/{id} |

## 3. 请求格式
### 3.1 请求头
Content-Type: application/json
Authorization: Bearer {token}
X-Request-Id: {uuid}

### 3.2 请求参数
- 路径参数：/users/{id}
- 查询参数：/users?page=1&size=10
- 请求体：JSON格式

## 4. 响应格式
### 4.1 成功响应
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": 1234567890
}

### 4.2 列表响应
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [],
    "total": 100,
    "page": 1,
    "size": 10
  },
  "timestamp": 1234567890
}

### 4.3 错误响应
{
  "code": 400,
  "message": "参数错误",
  "errors": [
    {
      "field": "userName",
      "message": "用户名不能为空"
    }
  ],
  "timestamp": 1234567890
}

## 5. 错误码
| 错误码 | 说明 | 使用场景 |
|--------|------|----------|
| 200 | 成功 | 请求成功 |
| 400 | 参数错误 | 请求参数错误 |
| 401 | 未认证 | 未登录或token过期 |
| 403 | 无权限 | 无访问权限 |
| 404 | 不存在 | 资源不存在 |
| 500 | 系统错误 | 服务器内部错误 |

## 6. 分页参数
| 参数 | 类型 | 说明 |
|------|------|------|
| page | int | 页码，从1开始 |
| size | int | 每页数量，默认10 |
| sort | string | 排序字段 |
| order | string | 排序方式：asc/desc |

## 7. 过滤参数
| 参数 | 类型 | 说明 |
|------|------|------|
| keyword | string | 关键字搜索 |
| status | string | 状态过滤 |
| startTime | datetime | 开始时间 |
| endTime | datetime | 结束时间 |
```

### API文档模板

```markdown
# API接口文档

## 接口信息
- 接口名称：[名称]
- 请求路径：[路径]
- 请求方法：[方法]
- 接口描述：[描述]

## 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| [参数] | [类型] | 是/否 | [说明] |

## 响应参数
| 参数名 | 类型 | 说明 |
|--------|------|------|
| [参数] | [类型] | [说明] |

## 请求示例
{
  "param1": "value1"
}

## 响应示例
{
  "code": 200,
  "data": {}
}
```

## Quality Checklist

- [ ] URL设计规范
- [ ] 请求响应格式统一
- [ ] 错误码定义完整
- [ ] 文档清晰易懂

## Common Mistakes

- URL设计不规范，使用动词或大小写混乱
- 响应格式不统一，不同接口返回结构不同
- 错误码定义不完整，缺少业务错误码
- 忽视分页设计，大数据量接口性能差
- 缺少接口版本控制，升级困难

## Quick Reference

| 设计要素 | 规范 | 示例 |
|----------|------|------|
| URL | 名词复数、小写 | /api/v1/users |
| GET | 查询 | GET /users |
| POST | 创建 | POST /users |
| PUT | 全量更新 | PUT /users/{id} |
| PATCH | 部分更新 | PATCH /users/{id} |
| DELETE | 删除 | DELETE /users/{id} |
| 响应格式 | 统一结构 | code + message + data |
| 错误码 | HTTP标准 | 200, 400, 401, 403, 500 |
| 分页 | page + size | ?page=1&size=10 |
