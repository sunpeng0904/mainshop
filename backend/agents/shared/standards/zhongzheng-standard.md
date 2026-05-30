# 中证开发规范

## 1. 概述

本规范定义了中证体系下的软件开发标准，所有开发活动必须遵守本规范。

## 2. 命名规范

### 2.1 项目命名
- 格式：`[组织]-[项目类型]-[项目名称]`
- 示例：`csb-system-payment`, `csb-api-user`

### 2.2 模块命名
- 格式：`[项目]-[层]-[模块]`
- 示例：`payment-service-order`, `user-dao-account`

### 2.3 类命名
- 大驼峰命名法（PascalCase）
- 示例：`OrderService`, `UserController`

### 2.4 方法命名
- 小驼峰命名法（camelCase）
- 动词开头：`getOrder`, `createUser`, `validateInput`

### 2.5 常量命名
- 全大写下划线分隔
- 示例：`MAX_RETRY_COUNT`, `DEFAULT_TIMEOUT`

### 2.6 数据库命名
- 表名：小写下划线分隔，`t_` 前缀
- 示例：`t_user`, `t_order_detail`
- 字段：小写下划线分隔
- 示例：`user_id`, `create_time`

## 3. 代码结构规范

### 3.1 分层架构
```
├── controller/        # 控制层 - 接收请求，参数校验
├── service/           # 业务层 - 业务逻辑
├── manager/           # 管理层 - 可复用业务逻辑
├── dao/               # 数据层 - 数据访问
├── model/             # 模型层 - 数据模型
│   ├── entity/        # 实体类
│   ├── dto/           # 数据传输对象
│   ├── vo/            # 视图对象
│   └── bo/            # 业务对象
├── common/            # 公共模块
│   ├── constant/      # 常量
│   ├── enum/          # 枚举
│   ├── exception/     # 异常
│   └── utils/         # 工具类
└── config/            # 配置
```

### 3.2 方法职责
- Controller 方法不超过 30 行
- Service 方法不超过 50 行
- Manager 方法不超过 80 行
- 超过需拆分或重构

## 4. 注释规范

### 4.1 类注释
```java
/**
 * 用户服务类
 * 处理用户相关的业务逻辑
 *
 * @author [作者]
 * @since [版本]
 * @date [日期]
 */
```

### 4.2 方法注释
```java
/**
 * 根据用户ID查询用户信息
 *
 * @param userId 用户ID
 * @return 用户信息
 * @throws BusinessException 用户不存在时抛出
 */
```

### 4.3 字段注释
```java
/** 用户ID */
private Long userId;

/** 创建时间 */
private LocalDateTime createTime;
```

## 5. 异常处理规范

### 5.1 异常分类
- 业务异常：`BusinessException`
- 系统异常：`SystemException`
- 参数异常：`IllegalArgumentException`

### 5.2 异常处理原则
- Controller 层统一捕获处理
- Service 层抛出业务异常
- 不要吞掉异常
- 异常信息要明确

## 6. 日志规范

### 6.1 日志级别
- ERROR：系统错误，需要立即处理
- WARN：警告信息，需要关注
- INFO：关键业务流程
- DEBUG：调试信息

### 6.2 日志内容
- 必须包含：时间、级别、类名、方法名
- 关键业务必须记录：入参、出参、耗时
- 异常必须记录：异常类型、堆栈信息

## 7. 接口规范

### 7.1 RESTful API
- GET：查询
- POST：创建
- PUT：全量更新
- PATCH：部分更新
- DELETE：删除

### 7.2 响应格式
```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": 1234567890
}
```

### 7.3 错误码
- 200：成功
- 400：参数错误
- 401：未认证
- 403：无权限
- 500：系统错误

## 8. 安全规范

### 8.1 输入校验
- 所有外部输入必须校验
- 使用白名单校验
- 防止 SQL 注入
- 防止 XSS 攻击

### 8.2 敏感数据
- 密码必须加密存储
- 敏感信息脱敏显示
- 日志不记录敏感信息

### 8.3 权限控制
- 最小权限原则
- 接口级别权限控制
- 数据级别权限控制

## 9. 数据库规范

### 9.1 表设计
- 必须有主键
- 必须有创建时间、更新时间
- 字段必须有注释
- 使用合适的字段类型

### 9.2 SQL 规范
- 禁止 SELECT *
- 使用参数化查询
- 避免大事务
- 索引规范

## 10. 代码审查清单

- [ ] 命名是否规范
- [ ] 注释是否完整
- [ ] 异常处理是否正确
- [ ] 日志记录是否充分
- [ ] 安全是否考虑
- [ ] 性能是否合理
- [ ] 代码是否可测试
