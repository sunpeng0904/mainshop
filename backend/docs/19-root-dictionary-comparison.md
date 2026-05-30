# 词根字典对照表

## 基本信息
- 项目名称：在线商城后端系统 - 地址管理模块优化
- 对照日期：2026-05-29
- 对照人：孙朋

## 1. 地址管理模块术语对照

### 1.1 实体类术语
| 中文术语 | 英文术语 | 词根字典标准 | 是否符合 | 说明 |
|----------|----------|--------------|----------|------|
| 用户地址 | UserAddress | user + address | 符合 | 用户地址实体 |
| 收货人姓名 | receiverName | receiver + name | 符合 | 收货人姓名字段 |
| 收货人电话 | receiverPhone | receiver + phone | 符合 | 收货人电话字段 |
| 省份编码 | provinceCode | province + code | 符合 | 省份编码字段 |
| 城市编码 | cityCode | city + code | 符合 | 城市编码字段 |
| 区县编码 | districtCode | district + code | 符合 | 区县编码字段 |
| 详细地址 | detailAddress | detail + address | 符合 | 详细地址字段 |
| 是否默认 | isDefault | is + default | 符合 | 默认地址标志 |
| 创建时间 | createTime | create + time | 符合 | 创建时间字段 |
| 更新时间 | updateTime | update + time | 符合 | 更新时间字段 |
| 删除标志 | deleted | deleted | 符合 | 逻辑删除标志 |

### 1.2 DTO术语
| 中文术语 | 英文术语 | 词根字典标准 | 是否符合 | 说明 |
|----------|----------|--------------|----------|------|
| 地址DTO | AddressDTO | address + DTO | 符合 | 地址数据传输对象 |
| 地址ID | id | id | 符合 | 地址主键 |
| 收货人姓名 | receiverName | receiver + name | 符合 | 收货人姓名 |
| 收货人电话 | receiverPhone | receiver + phone | 符合 | 收货人电话 |
| 省份编码 | provinceCode | province + code | 符合 | 省份编码 |
| 城市编码 | cityCode | city + code | 符合 | 城市编码 |
| 区县编码 | districtCode | district + code | 符合 | 区县编码 |
| 详细地址 | detailAddress | detail + address | 符合 | 详细地址 |
| 是否默认 | isDefault | is + default | 符合 | 是否默认 |

### 1.3 VO术语
| 中文术语 | 英文术语 | 词根字典标准 | 是否符合 | 说明 |
|----------|----------|--------------|----------|------|
| 地址VO | AddressVO | address + VO | 符合 | 地址视图对象 |
| 地址ID | id | id | 符合 | 地址主键 |
| 收货人姓名 | receiverName | receiver + name | 符合 | 收货人姓名 |
| 收货人电话 | receiverPhone | receiver + phone | 符合 | 收货人电话 |
| 省份编码 | provinceCode | province + code | 符合 | 省份编码 |
| 省份名称 | provinceName | province + name | 符合 | 省份名称 |
| 城市编码 | cityCode | city + code | 符合 | 城市编码 |
| 城市名称 | cityName | city + name | 符合 | 城市名称 |
| 区县编码 | districtCode | district + code | 符合 | 区县编码 |
| 区县名称 | districtName | district + name | 符合 | 区县名称 |
| 详细地址 | detailAddress | detail + address | 符合 | 详细地址 |
| 完整地址 | fullAddress | full + address | 符合 | 完整地址 |
| 是否默认 | isDefault | is + default | 符合 | 是否默认 |
| 创建时间 | createTime | create + time | 符合 | 创建时间 |
| 更新时间 | updateTime | update + time | 符合 | 更新时间 |

### 1.4 Service术语
| 中文术语 | 英文术语 | 词根字典标准 | 是否符合 | 说明 |
|----------|----------|--------------|----------|------|
| 用户地址服务 | UserAddressService | user + address + service | 符合 | 用户地址服务接口 |
| 获取地址列表 | getAddressList | get + address + list | 符合 | 获取地址列表方法 |
| 获取地址详情 | getAddressById | get + address + by + id | 符合 | 获取地址详情方法 |
| 添加地址 | addAddress | add + address | 符合 | 添加地址方法 |
| 更新地址 | updateAddress | update + address | 符合 | 更新地址方法 |
| 删除地址 | deleteAddress | delete + address | 符合 | 删除地址方法 |
| 设置默认地址 | setDefaultAddress | set + default + address | 符合 | 设置默认地址方法 |
| 获取默认地址 | getDefaultAddress | get + default + address | 符合 | 获取默认地址方法 |

### 1.5 Controller术语
| 中文术语 | 英文术语 | 词根字典标准 | 是否符合 | 说明 |
|----------|----------|--------------|----------|------|
| 地址控制器 | AddressController | address + controller | 符合 | 地址控制器 |
| 获取地址列表 | getAddressList | get + address + list | 符合 | 获取地址列表接口 |
| 获取地址详情 | getAddressById | get + address + by + id | 符合 | 获取地址详情接口 |
| 添加地址 | addAddress | add + address | 符合 | 添加地址接口 |
| 更新地址 | updateAddress | update + address | 符合 | 更新地址接口 |
| 删除地址 | deleteAddress | delete + address | 符合 | 删除地址接口 |
| 设置默认地址 | setDefaultAddress | set + default + address | 符合 | 设置默认地址接口 |
| 获取默认地址 | getDefaultAddress | get + default + address | 符合 | 获取默认地址接口 |

## 2. 数据库字段术语对照

### 2.1 用户地址表字段
| 中文术语 | 英文术语 | 数据库字段 | 词根字典标准 | 是否符合 | 修正方案 |
|----------|----------|------------|--------------|----------|----------|
| 主键ID | id | id | id | 符合 | - |
| 用户ID | userId | user_id | user + id | 符合 | - |
| 收货人姓名 | receiverName | receiver_name | receiver + name | 符合 | - |
| 收货人电话 | receiverPhone | receiver_phone | receiver + phone | 符合 | - |
| 省份编码 | provinceCode | province_code | province + code | 符合 | - |
| 城市编码 | cityCode | city_code | city + code | 符合 | - |
| 区县编码 | districtCode | district_code | district + code | 符合 | - |
| 详细地址 | detailAddress | detail_address | detail + address | 符合 | - |
| 是否默认 | isDefault | is_default | is + default | 符合 | - |
| 创建时间 | createTime | create_time | create + time | 符合 | - |
| 更新时间 | updateTime | update_time | update + time | 符合 | - |
| 删除标志 | deleted | deleted (int) | del_flag (char) | **不符合** | 修改为 `del_flag CHAR(1)` |
| 创建人 | createBy | 缺失 | create_by | **不符合** | 添加 `create_by VARCHAR(50)` |
| 更新人 | updateBy | 缺失 | update_by | **不符合** | 添加 `update_by VARCHAR(50)` |

### 2.2 区域表字段
| 中文术语 | 英文术语 | 数据库字段 | 词根字典标准 | 是否符合 | 修正方案 |
|----------|----------|------------|--------------|----------|----------|
| 主键ID | id | id | id | 符合 | - |
| 区域编码 | code | code | code | 符合 | - |
| 区域名称 | name | name | name | 符合 | - |
| 父级编码 | parentCode | parent_code | parent + code | 符合 | - |
| 级别 | level | level | level | 符合 | - |
| 删除标志 | deleted | deleted (int) | del_flag (char) | **不符合** | 修改为 `del_flag CHAR(1)` |
| 创建人 | createBy | 缺失 | create_by | **不符合** | 添加 `create_by VARCHAR(50)` |
| 更新人 | updateBy | 缺失 | update_by | **不符合** | 添加 `update_by VARCHAR(50)` |

## 3. API接口术语对照

### 3.1 接口路径
| 中文术语 | 英文术语 | API路径 | 词根字典标准 | 是否符合 |
|----------|----------|---------|--------------|----------|
| 地址列表 | address list | /address/list | address + list | 符合 |
| 地址详情 | address detail | /address/{id} | address + id | 符合 |
| 添加地址 | add address | /address/add | address + add | 符合 |
| 更新地址 | update address | /address/update | address + update | 符合 |
| 删除地址 | delete address | /address/{id} | address + id | 符合 |
| 默认地址 | default address | /address/default | address + default | 符合 |

### 3.2 请求参数
| 中文术语 | 英文术语 | 参数名 | 词根字典标准 | 是否符合 |
|----------|----------|--------|--------------|----------|
| 页码 | page | page | page | 符合 |
| 每页数量 | size | size | size | 符合 |
| 地址ID | addressId | addressId | address + id | 符合 |

### 3.3 响应参数
| 中文术语 | 英文术语 | 参数名 | 词根字典标准 | 是否符合 |
|----------|----------|--------|--------------|----------|
| 状态码 | code | code | code | 符合 |
| 提示信息 | message | message | message | 符合 |
| 数据 | data | data | data | 符合 |

## 4. 缓存Key术语对照

### 4.1 缓存Key
| 中文术语 | 英文术语 | 缓存Key | 词根字典标准 | 是否符合 |
|----------|----------|---------|--------------|----------|
| 用户地址列表 | user address list | user:address:list:{userId} | user + address + list | 符合 |
| 地址详情 | address detail | user:address:detail:{addressId} | user + address + detail | 符合 |
| 默认地址 | default address | user:address:default:{userId} | user + address + default | 符合 |

## 5. 对照结论

### 5.1 符合情况
- **总术语数**：56个
- **符合词根字典**：50个（89.3%）
- **不符合词根字典**：6个（10.7%）

### 5.2 不符合项汇总
| 序号 | 表名 | 字段 | 当前命名 | 词根规范 | 修正方案 |
|------|------|------|----------|----------|----------|
| 1 | user_address | 删除标志 | deleted (int) | del_flag (char) | 修改字段名和类型 |
| 2 | user_address | 创建人 | 缺失 | create_by | 添加字段 |
| 3 | user_address | 更新人 | 缺失 | update_by | 添加字段 |
| 4 | region | 删除标志 | deleted (int) | del_flag (char) | 修改字段名和类型 |
| 5 | region | 创建人 | 缺失 | create_by | 添加字段 |
| 6 | region | 更新人 | 缺失 | update_by | 添加字段 |

### 5.3 对照结论
- 业务字段命名规范，符合词根字典
- 系统字段（删除标志、创建人、更新人）不符合词根规范
- 需要执行修正脚本：`src/main/resources/db/address_table_fix.sql`

## 6. 修正方案

### 6.1 修正脚本
已创建修正脚本：`src/main/resources/db/address_table_fix.sql`

### 6.2 修正内容
1. 修改 `deleted` 字段为 `del_flag`，类型从 int 改为 char
2. 添加 `create_by` 字段
3. 添加 `update_by` 字段
4. 更新现有数据，将 deleted=0/1 转换为 del_flag='N'/'Y'

### 6.3 影响范围
- 数据库表结构变更
- 实体类字段需要同步修改
- MyBatis-Plus 逻辑删除配置需要更新

## 7. 建议
1. 执行修正脚本，使表字段符合词根规范
2. 同步修改实体类字段
3. 更新 MyBatis-Plus 逻辑删除配置
4. 新增术语时参考词根字典
5. 定期更新词根字典
