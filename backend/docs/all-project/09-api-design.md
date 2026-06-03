# API接口文档

## 基本信息
- 模块名称：地址管理
- 基础路径：/api/address
- 版本：v1
- 认证方式：JWT Token

## 接口列表

### 1. 获取地址列表

#### 接口信息
- 接口名称：获取地址列表
- 请求路径：/api/address/list
- 请求方法：GET
- 接口描述：获取当前用户的所有地址列表

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| page | int | 否 | 页码，默认1 |
| size | int | 否 | 每页数量，默认10 |

#### 响应参数
| 参数名 | 类型 | 说明 |
|--------|------|------|
| code | int | 状态码 |
| message | string | 提示信息 |
| data | array | 地址列表 |
| data[].id | long | 地址ID |
| data[].userId | long | 用户ID |
| data[].receiverName | string | 收货人姓名 |
| data[].receiverPhone | string | 手机号 |
| data[].provinceCode | string | 省编码 |
| data[].provinceName | string | 省名称 |
| data[].cityCode | string | 市编码 |
| data[].cityName | string | 市名称 |
| data[].districtCode | string | 区编码 |
| data[].districtName | string | 区名称 |
| data[].detailAddress | string | 详细地址 |
| data[].fullAddress | string | 完整地址 |
| data[].isDefault | int | 是否默认（0否1是） |
| data[].createTime | datetime | 创建时间 |

#### 请求示例
```
GET /api/address/list?page=1&size=10
Authorization: Bearer {token}
```

#### 响应示例
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "userId": 1001,
      "receiverName": "张三",
      "receiverPhone": "13800138000",
      "provinceCode": "110000",
      "provinceName": "北京市",
      "cityCode": "110100",
      "cityName": "北京市",
      "districtCode": "110105",
      "districtName": "朝阳区",
      "detailAddress": "某某路123号",
      "fullAddress": "北京市北京市朝阳区某某路123号",
      "isDefault": 1,
      "createTime": "2026-05-29 10:00:00"
    }
  ]
}
```

---

### 2. 获取地址详情

#### 接口信息
- 接口名称：获取地址详情
- 请求路径：/api/address/{addressId}
- 请求方法：GET
- 接口描述：获取指定地址的详细信息

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| addressId | long | 是 | 地址ID（路径参数） |

#### 响应参数
| 参数名 | 类型 | 说明 |
|--------|------|------|
| code | int | 状态码 |
| message | string | 提示信息 |
| data | object | 地址详情 |
| data.id | long | 地址ID |
| data.userId | long | 用户ID |
| data.receiverName | string | 收货人姓名 |
| data.receiverPhone | string | 手机号 |
| data.provinceCode | string | 省编码 |
| data.provinceName | string | 省名称 |
| data.cityCode | string | 市编码 |
| data.cityName | string | 市名称 |
| data.districtCode | string | 区编码 |
| data.districtName | string | 区名称 |
| data.detailAddress | string | 详细地址 |
| data.fullAddress | string | 完整地址 |
| data.isDefault | int | 是否默认（0否1是） |
| data.createTime | datetime | 创建时间 |

#### 请求示例
```
GET /api/address/1
Authorization: Bearer {token}
```

#### 响应示例
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "userId": 1001,
    "receiverName": "张三",
    "receiverPhone": "13800138000",
    "provinceCode": "110000",
    "provinceName": "北京市",
    "cityCode": "110100",
    "cityName": "北京市",
    "districtCode": "110105",
    "districtName": "朝阳区",
    "detailAddress": "某某路123号",
    "fullAddress": "北京市北京市朝阳区某某路123号",
    "isDefault": 1,
    "createTime": "2026-05-29 10:00:00"
  }
}
```

---

### 3. 添加地址

#### 接口信息
- 接口名称：添加地址
- 请求路径：/api/address/add
- 请求方法：POST
- 接口描述：添加新的收货地址

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| receiverName | string | 是 | 收货人姓名，最大50字符 |
| receiverPhone | string | 是 | 手机号，11位数字 |
| provinceCode | string | 是 | 省编码 |
| cityCode | string | 是 | 市编码 |
| districtCode | string | 是 | 区编码 |
| detailAddress | string | 是 | 详细地址，最大200字符 |
| isDefault | int | 否 | 是否默认（0否1是），默认0 |

#### 响应参数
| 参数名 | 类型 | 说明 |
|--------|------|------|
| code | int | 状态码 |
| message | string | 提示信息 |
| data | object | 添加的地址信息 |

#### 请求示例
```json
POST /api/address/add
Authorization: Bearer {token}
Content-Type: application/json

{
  "receiverName": "李四",
  "receiverPhone": "13900139000",
  "provinceCode": "310000",
  "cityCode": "310100",
  "districtCode": "310101",
  "detailAddress": "某某路456号",
  "isDefault": 0
}
```

#### 响应示例
```json
{
  "code": 200,
  "message": "添加成功",
  "data": {
    "id": 2,
    "userId": 1001,
    "receiverName": "李四",
    "receiverPhone": "13900139000",
    "provinceCode": "310000",
    "provinceName": "上海市",
    "cityCode": "310100",
    "cityName": "上海市",
    "districtCode": "310101",
    "districtName": "黄浦区",
    "detailAddress": "某某路456号",
    "fullAddress": "上海市上海市黄浦区某某路456号",
    "isDefault": 0,
    "createTime": "2026-05-29 10:30:00"
  }
}
```

---

### 4. 更新地址

#### 接口信息
- 接口名称：更新地址
- 请求路径：/api/address/update
- 请求方法：PUT
- 接口描述：更新收货地址信息

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | long | 是 | 地址ID |
| receiverName | string | 否 | 收货人姓名，最大50字符 |
| receiverPhone | string | 否 | 手机号，11位数字 |
| provinceCode | string | 否 | 省编码 |
| cityCode | string | 否 | 市编码 |
| districtCode | string | 否 | 区编码 |
| detailAddress | string | 否 | 详细地址，最大200字符 |
| isDefault | int | 否 | 是否默认（0否1是） |

#### 响应参数
| 参数名 | 类型 | 说明 |
|--------|------|------|
| code | int | 状态码 |
| message | string | 提示信息 |
| data | object | 更新后的地址信息 |

#### 请求示例
```json
PUT /api/address/update
Authorization: Bearer {token}
Content-Type: application/json

{
  "id": 1,
  "receiverName": "张三三",
  "isDefault": 1
}
```

#### 响应示例
```json
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "id": 1,
    "userId": 1001,
    "receiverName": "张三三",
    "receiverPhone": "13800138000",
    "provinceCode": "110000",
    "provinceName": "北京市",
    "cityCode": "110100",
    "cityName": "北京市",
    "districtCode": "110105",
    "districtName": "朝阳区",
    "detailAddress": "某某路123号",
    "fullAddress": "北京市北京市朝阳区某某路123号",
    "isDefault": 1,
    "createTime": "2026-05-29 10:00:00"
  }
}
```

---

### 5. 删除地址

#### 接口信息
- 接口名称：删除地址
- 请求路径：/api/address/{addressId}
- 请求方法：DELETE
- 接口描述：删除指定的收货地址

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| addressId | long | 是 | 地址ID（路径参数） |

#### 响应参数
| 参数名 | 类型 | 说明 |
|--------|------|------|
| code | int | 状态码 |
| message | string | 提示信息 |
| data | null | 无返回数据 |

#### 请求示例
```
DELETE /api/address/2
Authorization: Bearer {token}
```

#### 响应示例
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

---

### 6. 设置默认地址

#### 接口信息
- 接口名称：设置默认地址
- 请求路径：/api/address/default/{addressId}
- 请求方法：PUT
- 接口描述：设置指定地址为默认地址

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| addressId | long | 是 | 地址ID（路径参数） |

#### 响应参数
| 参数名 | 类型 | 说明 |
|--------|------|------|
| code | int | 状态码 |
| message | string | 提示信息 |
| data | null | 无返回数据 |

#### 请求示例
```
PUT /api/address/default/1
Authorization: Bearer {token}
```

#### 响应示例
```json
{
  "code": 200,
  "message": "设置成功",
  "data": null
}
```

---

### 7. 获取默认地址

#### 接口信息
- 接口名称：获取默认地址
- 请求路径：/api/address/default
- 请求方法：GET
- 接口描述：获取用户的默认地址

#### 请求参数
无

#### 响应参数
| 参数名 | 类型 | 说明 |
|--------|------|------|
| code | int | 状态码 |
| message | string | 提示信息 |
| data | object | 默认地址信息 |
| data.id | long | 地址ID |
| data.userId | long | 用户ID |
| data.receiverName | string | 收货人姓名 |
| data.receiverPhone | string | 手机号 |
| data.provinceCode | string | 省编码 |
| data.provinceName | string | 省名称 |
| data.cityCode | string | 市编码 |
| data.cityName | string | 市名称 |
| data.districtCode | string | 区编码 |
| data.districtName | string | 区名称 |
| data.detailAddress | string | 详细地址 |
| data.fullAddress | string | 完整地址 |
| data.isDefault | int | 是否默认（0否1是） |
| data.createTime | datetime | 创建时间 |

#### 请求示例
```
GET /api/address/default
Authorization: Bearer {token}
```

#### 响应示例
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "userId": 1001,
    "receiverName": "张三",
    "receiverPhone": "13800138000",
    "provinceCode": "110000",
    "provinceName": "北京市",
    "cityCode": "110100",
    "cityName": "北京市",
    "districtCode": "110105",
    "districtName": "朝阳区",
    "detailAddress": "某某路123号",
    "fullAddress": "北京市北京市朝阳区某某路123号",
    "isDefault": 1,
    "createTime": "2026-05-29 10:00:00"
  }
}
```

---

## 错误码

| 错误码 | 说明 | 使用场景 |
|--------|------|----------|
| 200 | 成功 | 请求成功 |
| 400 | 参数错误 | 请求参数错误 |
| 401 | 未认证 | 未登录或token过期 |
| 403 | 无权限 | 无访问权限 |
| 404 | 不存在 | 地址不存在 |
| 500 | 系统错误 | 服务器内部错误 |

## 业务错误码

| 错误码 | 说明 | 使用场景 |
|--------|------|----------|
| ADDRESS_NOT_FOUND | 地址不存在 | 地址ID错误或已被删除 |
| ADDRESS_LIMIT_EXCEEDED | 地址数量超限 | 用户地址数量达到上限 |
| INVALID_PHONE | 手机号格式错误 | 手机号不是11位数字 |
| INVALID_REGION_CODE | 区域编码错误 | 省市区编码不匹配 |
