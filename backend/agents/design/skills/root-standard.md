---
name: table-create
description: "当用户提到建表、表结构、DDL、CREATE TABLE，需要根据ATTRC2E.txt词根词典生成符合KingbaseV8R3规范的DDL时使用"
---

# 表结构生成

## Overview

根据业务规范文档 + ATTRC2E.txt 词根词典，生成符合 ERwin 标准的 KingbaseV8R3 DDL。采用三级匹配策略（整词 → 拆解 → 拼音）确保命名准确。

## When to Use

- 用户提到建表、表结构、DDL、CREATE TABLE
- 需要根据业务需求生成数据库表结构
- 需要使用ATTRC2E.txt词根词典进行命名转换
- 需要生成符合KingbaseV8R3规范的DDL

## Core Pattern

| 命名层级 | 用途 | 特点 |
|----------|------|------|
| Logical Name（逻辑名） | 业务沟通、文档 | 中文，面向业务人员 |
| Physical Name（物理名） | 数据库实际对象名 | 英文，遵循 ATTRC2E 词根 |

DDL 中 `comment on` 存储 Logical Name，`CREATE TABLE` 使用 Physical Name。

### 命名转换流程

业务需求（中文） → 确定 Logical Name（中文表名/列名） → 三级匹配：整词 → 拆解 → 拼音 → 拼合 Physical Name（英文） → 生成 DDL

## Implementation

1. **确认前提** - 确认 schema 名称（默认 `iasis`），读取业务规范文档理解数据模型
2. **确定表名** - 拆解中文业务语义为单词，用 GB2312 编码读取 ATTRC2E.txt，三级匹配词根，拼合成表名 + 后缀
3. **确定列名** - 列出所有业务字段中文名，逐个三级匹配词根，按 `{限定词}_{核心词}_{类型后缀}` 组合
4. **确定数据类型** - 根据场景选择类型（主键VARCHAR(32)、名称VARCHAR(100)~VARCHAR(500)、布尔CHAR(1)、日期DATE、金额DECIMAL(22,10)等）
5. **选择审计模板** - 基础审计(L2)含创建人/修改人信息，完整审计(L3)加部门/机构信息，数据导入表加版本/刷新时间
6. **输出 DDL** - `CREATE TABLE {schema}.{table_name}` + `ADD PRIMARY KEY` + `comment on`，所有列 NULL（主键除外），不使用 FOREIGN KEY 约束

## 关键注意事项

### ATTRC2E.txt 编码为 GB2312

**必须用 GB2312 编码读取**，不能用 UTF-8，否则中文全部乱码导致词根匹配失败。

```python
with open(attrc2e_path, 'r', encoding='gb2312', errors='ignore') as f:
    content = f.read()
```

### 词根匹配方法（三级策略）

**第一级：整词精确匹配**
```python
# 以"中文,"开头，避免模糊匹配到包含该词的其他词条
matches = [line.strip() for line in content.split('\n') if line.strip().startswith(word + ',')]
```

**第二级：拆解匹配**
```python
# 整词不存在时，拆成单字或更小的词，逐个查找
# "已推送" 整词不存在 → 拆解：已 + 推送
# 已 -> _already ✓, 推送 -> _send ✓
# 结果：already_send

# "廉政" 整词不存在 → 拆解：廉 + 政
# 廉 -> 不存在 ✗, 政 -> _gov ✓
# 廉仍无法匹配，保留拼音 lianzheng
```

**第三级：拼音兜底**
```python
# 拆解后仍有部分无法匹配，整体使用拼音
# 不要猜测或编造英文缩写
```

**匹配优先级**：整词匹配 > 拆解匹配 > 拼音兜底

### 表名后缀

| 后缀 | 含义 | 示例 |
|------|------|------|
| `_tb` | 通用业务表（默认） | `prjc_info_tb` |
| `_rcod` | 记录/日志表 | `send_rcod_tb` |
| `_cfg` | 配置/字典表 | `tmplt_cfg` |
| `_dtl` | 明细表 | `send_dtl_tb` |
| `_info` | 信息详情表 | `info_tb` |
| `_file` | 文件表 | `file_tb` |
| `_prsn` | 人员表 | `prsn_tb` |

### 审计字段模板

**基础审计（L2）**:
```sql
entr_psn_id VARCHAR(32) NULL,          -- 创建人标识
entr_psn_name VARCHAR(100) NULL,       -- 创建人姓名
entr_time TIMESTAMP NULL,               -- 创建时间
last_alter_psn_id VARCHAR(32) NULL,    -- 最后修改人标识
last_alter_psn_name VARCHAR(100) NULL, -- 最后修改人姓名
last_alter_time TIMESTAMP NULL          -- 最后修改时间
```

**数据导入表额外字段**:
```sql
version INTEGER NULL,        -- 版本号
renew_time TIMESTAMP NULL,   -- 数据刷新时间
import_time TIMESTAMP NULL,  -- 导入时间
data_src VARCHAR(10) NULL,   -- 数据来源
```

## 输出物

每次生成应输出：

1. **DDL 文件** (`*.sql`) — 可直接执行的建表语句
2. **映射表** (`*_映射表.md`) — Logical Name ↔ Physical Name 对照 + 词根依据

## Quality Checklist

- [ ] 每个字段是否经过三级匹配（整词→拆解→拼音）？
- [ ] 整词匹配是否精确（`词,` 开头，非模糊）？
- [ ] 拆解匹配是否逐字查找？
- [ ] 最终无法匹配的是否用了拼音（而非编造英文）？
- [ ] 表名后缀是否正确？
- [ ] 审计字段是否完整？
- [ ] comment on 是否每个列都有？
- [ ] 外键命名是否为 `{被引用表名}_id`？

## Common Mistakes

- 用 UTF-8 读取 ATTRC2E.txt 导致乱码匹配失败
- 整词匹配时使用模糊匹配而非精确匹配
- 无法匹配时编造英文缩写而非使用拼音
- 遗漏审计字段或审计字段不完整
- 使用 FOREIGN KEY 约束（项目规范不允许）
- 外键命名不规范

## Quick Reference

### 常见词根速查（已确认）

| 中文 | 词根 | 中文 | 词根 |
|------|------|------|------|
| 标识 | `_id` | 名称 | `_name` |
| 项目 | `_prjc` | 状态 | `_sts` |
| 日期 | `_date` | 时间 | `_time` |
| 说明 | `_dscr` | 结果 | `_result` |
| 文件 | `_file` | 处室 | `_dept` |
| 审核 | `_audit` | 内部 | `_insd` |
| 评价 | `_aprs` | 推送 | `_send` |
| 接收 | `_rcve` | 保荐 | `_rcmd` |
| 机构 | `_org` | 交易所 | `_exch` |
| 板块 | `_board` | 融资 | `_fin` |
| 类型 | `_type` | 版本 | `_version` |
| 来源 | `_src` | 数据 | `_data` |
| 主体 | `_pty` | 人员 | `_prsn` |
| 角色 | `_role` | 记录 | `_rcod` |
| 信息 | `_info` | 编号 | `_nbr` |
| 代码 | `_cde` | 分类 | `_clsf` |
| 描述 | `_describ` | 备注 | `_rmak` |
| 标志 | `_indc` | 有效 | `_vld` |
| 部门 | `_dept` | 路径 | `_path` |
| 大小 | `_size` | 目标 | `_trgt` |
| 明细 | `_dtal` | 已 | `_already` |
| 政 | `_gov` | 创建人 | `entr_psn` |
| 修改人 | `last_alter_psn` | 删除标志 | `vld_sts_cde` |

### 核心文件位置

- **词根词典**: `C:\Users\hspcadmin\Documents\zhongzhengjishu_data\Profiles\4973CE5A6649972A46C3828C6096B595\Cache\chat\file\202605\creatTableRule\ATTRC2E.txt`
- **表结构规范**: `C:\Users\hspcadmin\Documents\zhongzhengjishu_data\Profiles\4973CE5A6649972A46C3828C6096B595\Cache\chat\file\202605\creatTableRule\table-structure-spec.md`
- **ERwin 命名标准**: `skills/table_create/references/erwin-naming-standard.md`
