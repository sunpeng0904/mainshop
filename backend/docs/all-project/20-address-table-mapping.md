# 地址管理模块表结构映射表

## 基本信息
- 项目名称：在线商城后端系统 - 地址管理模块
- 生成日期：2026-05-29
- 词根依据：ATTRC2E.txt

## 1. 用户地址表映射 (user_addr_tb)

| Logical Name（逻辑名） | Physical Name（物理名） | 数据类型 | 词根依据 |
|------------------------|------------------------|----------|----------|
| 主键标识 | id | VARCHAR(32) | 标识 → _id |
| 用户标识 | user_id | VARCHAR(32) | 用户 → user, 标识 → _id |
| 收货人姓名 | rcvr_name | VARCHAR(100) | 收货 → rcvr, 姓名 → name |
| 收货人电话 | rcvr_tel | VARCHAR(20) | 收货 → rcvr, 电话 → tel |
| 省份编码 | prvc_cde | VARCHAR(10) | 省份 → prvc, 编码 → cde |
| 城市编码 | city_cde | VARCHAR(10) | 城市 → city, 编码 → cde |
| 区县编码 | dstrct_cde | VARCHAR(10) | 区县 → dstrct, 编码 → cde |
| 详细地址 | dtl_addr | VARCHAR(500) | 详细 → dtl, 地址 → addr |
| 是否默认标志 | dft_indc | CHAR(1) | 默认 → dft, 标志 → indc |
| 创建人标识 | entr_psn_id | VARCHAR(32) | 创建人标识 → entr_psn_id |
| 创建人姓名 | entr_psn_name | VARCHAR(100) | 创建人姓名 → entr_psn_name |
| 创建时间 | entr_time | TIMESTAMP | 创建时间 → entr_time |
| 最后修改人标识 | last_alter_psn_id | VARCHAR(32) | 最后修改人标识 → last_alter_psn_id |
| 最后修改人姓名 | last_alter_psn_name | VARCHAR(100) | 最后修改人姓名 → last_alter_psn_name |
| 最后修改时间 | last_alter_time | TIMESTAMP | 最后修改时间 → last_alter_time |
| 删除标志 | vld_sts_cde | CHAR(1) | 删除标志 → vld_sts_cde |

## 2. 区域表映射 (region_tb)

| Logical Name（逻辑名） | Physical Name（物理名） | 数据类型 | 词根依据 |
|------------------------|------------------------|----------|----------|
| 主键标识 | id | VARCHAR(32) | 标识 → _id |
| 区域编码 | cde | VARCHAR(10) | 编码 → cde |
| 区域名称 | name | VARCHAR(100) | 名称 → name |
| 父级编码 | prnt_cde | VARCHAR(10) | 父级 → prnt, 编码 → cde |
| 级别 | lvl | INTEGER | 级别 → lvl |
| 创建人标识 | entr_psn_id | VARCHAR(32) | 创建人标识 → entr_psn_id |
| 创建人姓名 | entr_psn_name | VARCHAR(100) | 创建人姓名 → entr_psn_name |
| 创建时间 | entr_time | TIMESTAMP | 创建时间 → entr_time |
| 最后修改人标识 | last_alter_psn_id | VARCHAR(32) | 最后修改人标识 → last_alter_psn_id |
| 最后修改人姓名 | last_alter_psn_name | VARCHAR(100) | 最后修改人姓名 → last_alter_psn_name |
| 最后修改时间 | last_alter_time | TIMESTAMP | 最后修改时间 → last_alter_time |
| 删除标志 | vld_sts_cde | CHAR(1) | 删除标志 → vld_sts_cde |

## 3. 词根速查

| 中文 | 词根 | 说明 |
|------|------|------|
| 标识 | _id | 主键/外键 |
| 名称 | _name | 名称字段 |
| 编码 | _cde | 编码字段 |
| 标志 | _indc | 标志位 |
| 级别 | _lvl | 等级 |
| 电话 | _tel | 联系电话 |
| 地址 | _addr | 地址 |
| 详细 | _dtl | 详细 |
| 默认 | _dft | 默认 |
| 省份 | _prvc | 省 |
| 城市 | _city | 市 |
| 区县 | _dstrct | 区 |
| 收货 | _rcvr | 收货人 |
| 用户 | _user | 用户 |
| 父级 | _prnt | 父级 |
| 创建人标识 | entr_psn_id | 审计字段 |
| 创建人姓名 | entr_psn_name | 审计字段 |
| 创建时间 | entr_time | 审计字段 |
| 最后修改人标识 | last_alter_psn_id | 审计字段 |
| 最后修改人姓名 | last_alter_psn_name | 审计字段 |
| 最后修改时间 | last_alter_time | 审计字段 |
| 删除标志 | vld_sts_cde | 逻辑删除 |

## 4. 与原表结构对比

| 原字段名 | 新字段名 | 变更说明 |
|----------|----------|----------|
| id (BIGINT) | id (VARCHAR(32)) | 类型变更 |
| user_id | user_id | 保持 |
| receiver_name | rcvr_name | 词根简化 |
| receiver_phone | rcvr_tel | 词根变更 |
| province_code | prvc_cde | 词根变更 |
| city_code | city_cde | 词根变更 |
| district_code | dstrct_cde | 词根变更 |
| detail_address | dtl_addr | 词根简化 |
| is_default | dft_indc | 词根变更 |
| create_time | entr_time | 审计字段规范 |
| update_time | last_alter_time | 审计字段规范 |
| create_by | entr_psn_id + entr_psn_name | 拆分为ID和姓名 |
| update_by | last_alter_psn_id + last_alter_psn_name | 拆分为ID和姓名 |
| deleted | vld_sts_cde | 词根变更 |
| 表名 user_address | 表名 user_addr_tb | 添加_tb后缀 |
| 表名 region | 表名 region_tb | 添加_tb后缀 |
