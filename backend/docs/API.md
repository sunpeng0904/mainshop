# API 接口文档

## 基础信息

- Base URL: `http://localhost:8082/api`
- 认证方式: JWT Token (Header: `Authorization: Bearer <token>`)
- 响应格式: JSON

## 通用响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

## 错误码

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 参数错误 |
| 401 | 未认证 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器错误 |

---

## 用户模块 `/user`

### 用户注册
```
POST /user/register
Content-Type: application/json

Request:
{
  "username": "test",
  "password": "123456",
  "email": "test@example.com",
  "phone": "13800138000"
}

Response:
{
  "code": 200,
  "data": {
    "id": 1,
    "username": "test",
    "email": "test@example.com"
  }
}
```

### 用户登录
```
POST /user/login
Content-Type: application/json

Request:
{
  "username": "test",
  "password": "123456"
}

Response:
{
  "code": 200,
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "user": { ... }
  }
}
```

### 获取用户信息
```
GET /user/info
Authorization: Bearer <token>

Response:
{
  "code": 200,
  "data": {
    "id": 1,
    "username": "test",
    "email": "test@example.com",
    "phone": "13800138000",
    "roles": ["ROLE_USER"]
  }
}
```

### 更新用户信息
```
PUT /user/info
Authorization: Bearer <token>
Content-Type: application/json

Request:
{
  "email": "new@example.com",
  "phone": "13900139000"
}
```

### 修改密码
```
PUT /user/password
Authorization: Bearer <token>
Content-Type: application/json

Request:
{
  "oldPassword": "123456",
  "newPassword": "654321"
}
```

---

## 商品模块 `/product`

### 获取商品列表
```
GET /product/list?pageNum=1&pageSize=10&categoryId=1&keyword=手机

Response:
{
  "code": 200,
  "data": {
    "records": [...],
    "total": 100,
    "pages": 10,
    "current": 1
  }
}
```

### 获取商品详情
```
GET /product/{id}

Response:
{
  "code": 200,
  "data": {
    "id": 1,
    "name": "iPhone 15",
    "price": 5999.00,
    "stock": 100,
    "description": "...",
    "images": [...]
  }
}
```

### 搜索商品
```
GET /product/search?keyword=iPhone&pageNum=1&pageSize=10
```

---

## 分类模块 `/category`

### 获取分类树
```
GET /category/tree

Response:
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "name": "电子产品",
      "children": [
        { "id": 2, "name": "手机" },
        { "id": 3, "name": "电脑" }
      ]
    }
  ]
}
```

### 获取所有分类
```
GET /category/all
```

---

## 购物车模块 `/cart`

### 获取购物车
```
GET /cart
Authorization: Bearer <token>

Response:
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "productId": 1,
      "productName": "iPhone 15",
      "price": 5999.00,
      "quantity": 2,
      "subtotal": 11998.00
    }
  ]
}
```

### 添加到购物车
```
POST /cart
Authorization: Bearer <token>
Content-Type: application/json

Request:
{
  "productId": 1,
  "quantity": 1
}
```

### 更新购物车商品数量
```
PUT /cart/{id}
Authorization: Bearer <token>
Content-Type: application/json

Request:
{
  "quantity": 3
}
```

### 删除购物车商品
```
DELETE /cart/{id}
Authorization: Bearer <token>
```

### 清空购物车
```
DELETE /cart/clear
Authorization: Bearer <token>
```

---

## 订单模块 `/order`

### 创建订单
```
POST /order
Authorization: Bearer <token>
Content-Type: application/json

Request:
{
  "addressId": 1,
  "remark": "尽快发货"
}

Response:
{
  "code": 200,
  "data": {
    "orderId": "202604160001",
    "totalAmount": 11998.00
  }
}
```

### 获取订单列表
```
GET /order/list?pageNum=1&pageSize=10&status=1
Authorization: Bearer <token>
```

### 获取订单详情
```
GET /order/{orderId}
Authorization: Bearer <token>
```

### 取消订单
```
PUT /order/{orderId}/cancel
Authorization: Bearer <token>
```

### 确认收货
```
PUT /order/{orderId}/confirm
Authorization: Bearer <token>
```

---

## 支付模块 `/payment`

### 创建支付
```
POST /payment/create
Authorization: Bearer <token>
Content-Type: application/json

Request:
{
  "orderId": "202604160001",
  "paymentMethod": "WECHAT"
}

Response:
{
  "code": 200,
  "data": {
    "paymentId": 1,
    "qrCodeUrl": "weixin://wxpay/..."
  }
}
```

### 查询支付状态
```
GET /payment/{paymentId}/status
Authorization: Bearer <token>
```

### 微信支付回调
```
POST /payment/wechat/callback
Content-Type: application/json
```

---

## 地址模块 `/address`

### 获取地址列表
```
GET /address
Authorization: Bearer <token>
```

### 添加地址
```
POST /address
Authorization: Bearer <token>
Content-Type: application/json

Request:
{
  "receiverName": "张三",
  "receiverPhone": "13800138000",
  "provinceCode": "110000",
  "cityCode": "110100",
  "districtCode": "110105",
  "detailAddress": "望京SOHO T1 1001室",
  "isDefault": 1
}
```

### 更新地址
```
PUT /address
Authorization: Bearer <token>
Content-Type: application/json

Request:
{
  "id": 1,
  "receiverName": "李四",
  ...
}
```

### 删除地址
```
DELETE /address/{id}
Authorization: Bearer <token>
```

### 设置默认地址
```
PUT /address/{id}/default
Authorization: Bearer <token>
```

---

## 区域模块 `/region`

### 获取所有省份
```
GET /region/provinces

Response:
{
  "code": 200,
  "data": [
    { "regionCode": "110000", "regionName": "北京市" },
    { "regionCode": "310000", "regionName": "上海市" }
  ]
}
```

### 获取城市列表
```
GET /region/cities/{provinceCode}
```

### 获取区县列表
```
GET /region/districts/{cityCode}
```

---

## 抽奖模块 `/lottery`

### 获取奖品列表
```
GET /lottery/prizes

Response:
{
  "code": 200,
  "data": [
    { "id": 1, "name": "笔记本电脑", "level": 1, "probability": 0.1 },
    { "id": 2, "name": "智能手机", "level": 2, "probability": 1.0 }
  ]
}
```

### 执行抽奖
```
POST /lottery/draw
Authorization: Bearer <token>

Response:
{
  "code": 200,
  "data": {
    "win": true,
    "prizeId": 3,
    "prizeName": "保温杯",
    "prizeLevel": 3,
    "rotateAngle": 1850,
    "remainingTimes": 2
  }
}
```

### 获取抽奖记录
```
GET /lottery/records
Authorization: Bearer <token>
```

### 领取奖品
```
POST /lottery/receive/{recordId}
Authorization: Bearer <token>
```

### 获取剩余抽奖次数
```
GET /lottery/times
Authorization: Bearer <token>
```

---

## 管理端接口 `/admin`

> 需要管理员角色 (ROLE_ADMIN)

### 商品管理

```
POST   /admin/product        # 创建商品
PUT    /admin/product/{id}   # 更新商品
DELETE /admin/product/{id}   # 删除商品
PUT    /admin/product/{id}/status # 更新状态
```

### 分类管理

```
POST   /admin/category        # 创建分类
PUT    /admin/category/{id}   # 更新分类
DELETE /admin/category/{id}   # 删除分类
```

### 订单管理

```
GET    /admin/order/list      # 订单列表
PUT    /admin/order/{id}/status # 更新订单状态
GET    /admin/order/statistics # 订单统计
```

### 用户管理

```
GET    /admin/user/list       # 用户列表
PUT    /admin/user/{id}/status # 更新用户状态
PUT    /admin/user/{id}/roles  # 分配角色
```

### 角色管理

```
GET    /admin/role/list       # 角色列表
POST   /admin/role            # 创建角色
PUT    /admin/role/{id}       # 更新角色
DELETE /admin/role/{id}       # 删除角色
```

### 抽奖管理

```
GET    /admin/lottery/prizes   # 奖品列表
POST   /admin/lottery/prize    # 创建奖品
PUT    /admin/lottery/prize/{id} # 更新奖品
GET    /admin/lottery/records  # 抽奖记录
```

### 文件上传

```
POST   /admin/upload/image    # 上传图片
POST   /admin/upload/file     # 上传文件
```
