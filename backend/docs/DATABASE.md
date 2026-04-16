# 数据库设计文档

## 数据库信息

- 数据库名: `online_mall`
- 字符集: `utf8mb4`
- 排序规则: `utf8mb4_unicode_ci`

---

## 表结构

### 用户表 `user`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| username | VARCHAR(50) | 用户名，唯一 |
| password | VARCHAR(255) | 密码（加密） |
| email | VARCHAR(100) | 邮箱 |
| phone | VARCHAR(20) | 手机号 |
| avatar | VARCHAR(255) | 头像URL |
| status | INT | 状态：0禁用 1启用 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |
| deleted | INT | 逻辑删除：0正常 1删除 |

**索引**: `uk_username(username)`

---

### 角色表 `t_role`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| role_code | VARCHAR(50) | 角色编码（ROLE_ADMIN, ROLE_USER） |
| role_name | VARCHAR(50) | 角色名称 |
| description | VARCHAR(255) | 描述 |
| sort | INT | 排序 |
| status | INT | 状态 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |
| deleted | INT | 逻辑删除 |

---

### 用户角色关联表 `user_role`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 用户ID |
| role_id | BIGINT | 角色ID |
| create_time | DATETIME | 创建时间 |

**索引**: `idx_user_id(user_id)`, `idx_role_id(role_id)`

---

### 商品分类表 `category`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| name | VARCHAR(50) | 分类名称 |
| parent_id | BIGINT | 父分类ID（0为顶级） |
| level | INT | 层级：1一级 2二级 3三级 |
| sort | INT | 排序 |
| icon | VARCHAR(255) | 图标 |
| status | INT | 状态 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |
| deleted | INT | 逻辑删除 |

---

### 商品表 `product`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| name | VARCHAR(200) | 商品名称 |
| category_id | BIGINT | 分类ID |
| price | DECIMAL(10,2) | 价格 |
| original_price | DECIMAL(10,2) | 原价 |
| stock | INT | 库存 |
| sales | INT | 销量 |
| description | TEXT | 描述 |
| main_image | VARCHAR(255) | 主图 |
| images | TEXT | 图片列表（JSON） |
| status | INT | 状态：0下架 1上架 |
| sort | INT | 排序 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |
| deleted | INT | 逻辑删除 |

**索引**: `idx_category_id(category_id)`, `idx_status(status)`

---

### 购物车表 `cart`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 用户ID |
| product_id | BIGINT | 商品ID |
| quantity | INT | 数量 |
| selected | INT | 是否选中：0否 1是 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

**索引**: `idx_user_id(user_id)`, `uk_user_product(user_id, product_id)`

---

### 订单表 `order`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| order_no | VARCHAR(50) | 订单号 |
| user_id | BIGINT | 用户ID |
| total_amount | DECIMAL(10,2) | 总金额 |
| pay_amount | DECIMAL(10,2) | 实付金额 |
| status | INT | 状态：0待付款 1待发货 2待收货 3已完成 4已取消 |
| payment_method | VARCHAR(20) | 支付方式 |
| payment_time | DATETIME | 支付时间 |
| delivery_time | DATETIME | 发货时间 |
| receive_time | DATETIME | 收货时间 |
| receiver_name | VARCHAR(50) | 收货人 |
| receiver_phone | VARCHAR(20) | 收货电话 |
| address | VARCHAR(500) | 收货地址 |
| remark | VARCHAR(255) | 备注 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |
| deleted | INT | 逻辑删除 |

**索引**: `uk_order_no(order_no)`, `idx_user_id(user_id)`, `idx_status(status)`

---

### 订单项表 `order_item`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| order_id | BIGINT | 订单ID |
| product_id | BIGINT | 商品ID |
| product_name | VARCHAR(200) | 商品名称 |
| product_image | VARCHAR(255) | 商品图片 |
| price | DECIMAL(10,2) | 单价 |
| quantity | INT | 数量 |
| total_amount | DECIMAL(10,2) | 小计 |
| create_time | DATETIME | 创建时间 |

**索引**: `idx_order_id(order_id)`

---

### 支付表 `payment`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| payment_no | VARCHAR(50) | 支付单号 |
| order_id | BIGINT | 订单ID |
| user_id | BIGINT | 用户ID |
| amount | DECIMAL(10,2) | 支付金额 |
| payment_method | VARCHAR(20) | 支付方式：WECHAT |
| status | INT | 状态：0待支付 1已支付 2已退款 |
| transaction_id | VARCHAR(100) | 第三方交易号 |
| pay_time | DATETIME | 支付时间 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

**索引**: `uk_payment_no(payment_no)`, `idx_order_id(order_id)`

---

### 用户地址表 `user_address`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 用户ID |
| receiver_name | VARCHAR(50) | 收货人姓名 |
| receiver_phone | VARCHAR(20) | 收货人电话 |
| province_code | VARCHAR(10) | 省份编码 |
| city_code | VARCHAR(10) | 城市编码 |
| district_code | VARCHAR(10) | 区县编码 |
| detail_address | VARCHAR(200) | 详细地址 |
| is_default | INT | 是否默认：0否 1是 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |
| deleted | INT | 逻辑删除 |

**索引**: `idx_user_id(user_id)`

---

### 区域表 `sys_region`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| region_code | VARCHAR(10) | 区域编码（国标码） |
| region_name | VARCHAR(50) | 区域名称 |
| parent_code | VARCHAR(10) | 父级编码 |
| level | INT | 层级：1省 2市 3区 |
| sort | INT | 排序 |
| status | INT | 状态 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |
| deleted | INT | 逻辑删除 |

**索引**: `uk_region_code(region_code)`, `idx_parent_code(parent_code)`

---

### 抽奖奖品表 `lottery_prize`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| name | VARCHAR(100) | 奖品名称 |
| level | INT | 等级：1一等奖 2二等奖 3三等奖 4谢谢参与 |
| image | VARCHAR(255) | 奖品图片 |
| value | DECIMAL(10,2) | 奖品价值 |
| probability | DECIMAL(5,2) | 中奖概率（%） |
| stock | INT | 库存数量 |
| issued | INT | 已发放数量 |
| version | INT | 乐观锁版本号 |
| status | INT | 状态 |
| sort | INT | 排序 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

**索引**: `idx_status(status)`

---

### 抽奖记录表 `lottery_record`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 用户ID |
| username | VARCHAR(50) | 用户名 |
| prize_id | BIGINT | 奖品ID |
| prize_name | VARCHAR(100) | 奖品名称 |
| prize_level | INT | 奖品等级 |
| lottery_time | DATETIME | 抽奖时间 |
| receive_status | INT | 领取状态：0未领取 1已领取 |
| receive_time | DATETIME | 领取时间 |
| remark | VARCHAR(255) | 备注 |
| create_time | DATETIME | 创建时间 |

**索引**: `idx_user_id(user_id)`, `idx_lottery_time(lottery_time)`

---

## ER 关系图

```
┌─────────┐     ┌─────────┐     ┌─────────┐
│  User   │────<│UserRole │>────│  Role   │
└─────────┘     └─────────┘     └─────────┘
     │
     │
     ├──────────────────────┐
     │                      │
     ▼                      ▼
┌─────────┐           ┌─────────┐
│Address  │           │  Cart   │
└─────────┘           └─────────┘
                              │
                              ▼
                         ┌─────────┐
                         │ Product │
                         └─────────┘
                              │
     ┌────────────────────────┤
     │                        │
     ▼                        ▼
┌─────────┐            ┌──────────┐
│Category │            │OrderItem │
└─────────┘            └──────────┘
                            │
                            ▼
                       ┌─────────┐
                       │  Order  │
                       └─────────┘
                            │
                            ▼
                       ┌─────────┐
                       │ Payment │
                       └─────────┘
```

---

## 数据库迁移脚本

| 脚本 | 说明 |
|------|------|
| db/region.sql | 区域表结构+全国省市区数据 |
| db/lottery.sql | 抽奖表结构+初始奖品数据 |
