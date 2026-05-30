# 词根定义字典

## 概述

本字典定义了项目中使用的所有标准词根，确保术语一致性。所有命名必须使用本字典中的词根。

## 通用词根

| 词根 | 英文 | 含义 | 示例 |
|------|------|------|------|
| 用户 | user | 系统用户 | userId, userName |
| 账户 | account | 登录账户 | accountId, accountName |
| 角色 | role | 用户角色 | roleId, roleName |
| 权限 | permission | 操作权限 | permissionId, permissionCode |
| 菜单 | menu | 系统菜单 | menuId, menuName |
| 组织 | org | 组织机构 | orgId, orgName |
| 部门 | dept | 部门 | deptId, deptName |
| 字典 | dict | 数据字典 | dictId, dictCode |
| 配置 | config | 系统配置 | configId, configKey |
| 日志 | log | 操作日志 | logId, logContent |

## 业务词根

| 词根 | 英文 | 含义 | 示例 |
|------|------|------|------|
| 订单 | order | 业务订单 | orderId, orderNo |
| 产品 | product | 产品 | productId, productName |
| 商品 | goods | 商品 | goodsId, goodsName |
| 价格 | price | 价格 | priceId, priceAmount |
| 支付 | payment | 支付 | paymentId, paymentStatus |
| 退款 | refund | 退款 | refundId, refundAmount |
| 客户 | customer | 客户 | customerId, customerName |
| 供应商 | supplier | 供应商 | supplierId, supplierName |
| 合同 | contract | 合同 | contractId, contractNo |
| 发票 | invoice | 发票 | invoiceId, invoiceNo |

## 技术词根

| 词根 | 英文 | 含义 | 示例 |
|------|------|------|------|
| 请求 | request | HTTP请求 | requestId, requestBody |
| 响应 | response | HTTP响应 | responseCode, responseData |
| 会话 | session | 会话 | sessionId, sessionToken |
| 令牌 | token | 认证令牌 | tokenValue, tokenExpiry |
| 缓存 | cache | 缓存 | cacheKey, cacheValue |
| 队列 | queue | 消息队列 | queueName, queueMessage |
| 任务 | task | 异步任务 | taskId, taskStatus |
| 事件 | event | 事件 | eventId, eventType |
| 消息 | message | 消息 | messageId, messageContent |
| 通知 | notification | 通知 | notificationId, notificationType |

## 状态词根

| 词根 | 英文 | 含义 | 枚举值 |
|------|------|------|--------|
| 状态 | status | 通用状态 | 0-禁用, 1-启用 |
| 类型 | type | 通用类型 | 自定义枚举 |
| 级别 | level | 等级 | 1-低, 2-中, 3-高 |
| 阶段 | phase | 阶段 | 自定义枚举 |
| 结果 | result | 结果 | success, failure |
| 标志 | flag | 标志位 | Y-是, N-否 |

## 时间词根

| 词根 | 英文 | 含义 | 示例 |
|------|------|------|------|
| 创建时间 | createTime | 记录创建时间 | create_time |
| 更新时间 | updateTime | 记录更新时间 | update_time |
| 删除时间 | deleteTime | 逻辑删除时间 | delete_time |
| 开始时间 | startTime | 开始时间 | start_time |
| 结束时间 | endTime | 结束时间 | end_time |
| 生效时间 | effectTime | 生效时间 | effect_time |
| 失效时间 | expireTime | 失效时间 | expire_time |

## 操作词根

| 词根 | 英文 | 含义 | 示例 |
|------|------|------|------|
| 创建 | create | 新增记录 | createOrder |
| 更新 | update | 修改记录 | updateUser |
| 删除 | delete | 删除记录 | deleteUser |
| 查询 | query | 查询记录 | queryList |
| 搜索 | search | 搜索 | searchKeyword |
| 导入 | import | 数据导入 | importExcel |
| 导出 | export | 数据导出 | exportExcel |
| 审核 | audit | 审核 | auditRecord |
| 提交 | submit | 提交 | submitForm |
| 撤回 | revoke | 撤回 | revokeSubmit |

## 数据库字段词根

| 词根 | 英文 | 类型 | 说明 |
|------|------|------|------|
| id | id | bigint | 主键 |
| 编号 | no | varchar | 业务编号 |
| 名称 | name | varchar | 名称 |
| 描述 | description | text | 描述 |
| 备注 | remark | text | 备注 |
| 排序 | sort | int | 排序号 |
| 版本 | version | int | 版本号 |
| 删除标志 | delFlag | char | 逻辑删除标志 |
| 创建人 | createBy | varchar | 创建人 |
| 更新人 | updateBy | varchar | 更新人 |

## 使用规则

1. **必须使用** - 所有命名必须使用本字典中的词根
2. **组合使用** - 复杂概念使用词根组合
3. **保持一致** - 相同含义使用相同词根
4. **新增申请** - 新词根需要提交申请并更新字典
