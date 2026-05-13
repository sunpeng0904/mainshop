# 【PRD】分享（朋友圈）功能需求文档

> **文档版本：** v2.0  
> **文档状态：** 评审中  
> **最后更新：** 2026-05-09  
> **优先级：** P0  
> **产品负责人：** 待定  

---

## 目录

- [1. 文档概述](#1-文档概述)
- [2. 产品背景](#2-产品背景)
- [3. 用户角色与场景](#3-用户角色与场景)
- [4. 功能模块详述](#4-功能模块详述)
- [5. 业务规则与约束](#5-业务规则与约束)
- [6. 数据库设计](#6-数据库设计)
- [7. API 接口设计](#7-api-接口设计)
- [8. 通知体系](#8-通知体系)
- [9. 内容安全与审核](#9-内容安全与审核)
- [10. 性能与技术指标](#10-性能与技术指标)
- [11. 版本规划与里程碑](#11-版本规划与里程碑)
- [附录 A：状态机定义](#附录-a状态机定义)
- [附录 B：异常码规范](#附录-b异常码规范)

---

## 1. 文档概述

### 1.1 背景与目标

本项目为电商/社交平台新增「分享」功能模块（对标微信朋友圈），核心目标：

| 维度 | 目标 | 衡量指标 |
|------|------|----------|
| 用户增长 | 通过社交分享提升平台活跃度 | DAU 提升 ≥ 15% |
| 内容生态 | 建立 UGC 内容生产-消费闭环 | 日均分享发布量 ≥ 5,000 条 |
| 社交留存 | 通过互动（点赞/评论/转发）提升留存 | 次日留存率提升 ≥ 8% |
| 商业变现 | 为后续信息流广告、内容电商铺路 | — |

### 1.2 名词定义

| 术语 | 定义 |
|------|------|
| **圈子** | 用户个人主页空间，聚合该用户发布的所有分享内容 |
| **好友圈** | 基于双向关注关系的动态信息流 |
| **动态** | 分享内容在信息流中的展示单元 |
| **可见范围** | 控制分享内容的曝光权限（公开/好友/部分/仅自己） |
| **转发** | 引用原分享内容并附带评论，生成新分享条目 |

### 1.3 文档约束

- 本需求基于现有项目 `online-mall` 后端仓库
- 技术栈：Spring Boot + MyBatis-Plus + MySQL 8.0 + Redis
- 前端后续由设计文档驱动生成，本 PRD 聚焦后端接口与业务逻辑

---

## 2. 产品背景

### 2.1 竞品对标

| 平台 | 功能 | 核心差异 |
|------|------|----------|
| 微信朋友圈 | 文本/图片/视频/链接分享 | 强隐私控制（仅共同好友可见） |
| 抖音关注页 | 图文/视频动态 | 算法推荐驱动 |
| 小红书笔记 | 图文/视频笔记 | 内容标签 + 搜索分发 |

**本平台定位：** 以「熟人社交 + 内容分享」为核心，参考微信朋友圈交互模式，但增加话题标签等公域分发能力，兼顾社交与内容属性。

### 2.2 用户画像

| 用户类型 | 特征 | 核心需求 |
|----------|------|----------|
| 活跃分享者 | 每周发布 ≥ 3 条动态 | 表达欲强、关注互动反馈 |
| 内容消费者 | 日均浏览 ≥ 30 分钟 | 了解好友动态、发现有趣内容 |
| 新用户 | 注册未发过内容 | 被好友互动吸引、逐步参与 |
| 商家/品牌号 | 企业认证用户 | 品牌宣传、产品推广 |

---

## 3. 用户角色与场景

### 3.1 用户角色

| 角色 | 权限 | 操作范围 |
|------|------|----------|
| **游客** | 浏览公开内容 | 查看公开分享、无法互动 |
| **普通用户** | 完整互动权限 | 发布、点赞、评论、转发、收藏 |
| **内容审核员** | 审核管理 | 审核待发布内容、处理举报 |
| **平台管理员** | 最高权限 | 内容管理、用户管理、系统配置 |

### 3.2 核心用户旅程

```
用户打开 App
    │
    ├── 首页 → 好友圈信息流 → 浏览动态 → 点赞/评论/转发
    │
    ├── 发布入口 → 选择图片 → 编辑内容 → 设置可见范围 → 发布
    │
    ├── 个人主页 → 我的圈子 → 管理已发布内容
    │
    └── 消息中心 → 通知列表 → 互动消息推送
```

### 3.3 典型场景

| 场景编号 | 场景名称 | 用户操作 | 预期结果 |
|----------|----------|----------|----------|
| S01 | 发布图文分享 | 选择图片 + 填写文字 + 点击发布 | 内容发布至圈子，好友可在信息流看到 |
| S02 | 可见范围控制 | 发布时选择"仅好友可见" | 非好友无法查看该分享 |
| S03 | 点赞互动 | 点击爱心图标 | 点赞数 +1，通知分享者 |
| S04 | 评论互动 | 输入评论并发送 | 评论出现，通知分享者 |
| S05 | 转发分享 | 点击转发 + 添加评论 | 生成新分享，引用原内容 |
| S06 | 收藏内容 | 点击收藏图标 | 内容加入"我的收藏" |
| S07 | 删除自己的分享 | 长按分享 → 删除 | 内容标记删除，信息流不再展示 |
| S08 | 举报违规内容 | 长按分享 → 举报 | 进入审核队列，审核员处理 |

---

## 4. 功能模块详述

### 4.1 模块总览

```
┌──────────────────────────────────────────────────────────────────────┐
│                         分享功能模块架构                               │
├──────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐            │
│  │ 内容发布  │  │ 内容浏览  │  │ 互动模块  │  │ 社交关系  │            │
│  │ Module   │  │ Module   │  │ Module   │  │ Module   │            │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬─────┘            │
│       │              │              │              │                  │
│       └──────────────┴──────────────┴──────────────┘                  │
│                              │                                        │
│                              ▼                                        │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐            │
│  │ 通知推送  │  │ 内容审核  │  │ 数据统计  │  │ 安全风控  │            │
│  │ Module   │  │ Module   │  │ Module   │  │ Module   │            │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘            │
│                                                                      │
└──────────────────────────────────────────────────────────────────────┘
```

---

### 4.2 内容发布模块

#### 4.2.1 文本分享

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| content | String | 是 | 文本内容，最大 **1000** 字，支持 emoji |
| location | String | 否 | 所在位置，最大 **100** 字 |
| visibility | Enum | 否 | 可见范围，默认 `FRIENDS`（好友可见） |
| mentionUserIds | List\<Long\> | 否 | @的好友 ID 列表，最多 **10** 人 |
| topicTags | List\<String\> | 否 | 话题标签，如 `#健身打卡`，最多 **5** 个 |

**业务规则：**

1. 文本内容需经过敏感词库过滤（阿里云内容安全 + 自定义词库）
2. `@好友` 仅限互相关注的好友
3. 被 @ 用户收到 `MENTION` 类型通知
4. 话题标签支持点击跳转话题聚合页
5. 文本换行保留原始格式
6. 支持链接自动识别并生成预览卡片

#### 4.2.2 图片分享

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| imageUrls | List\<String\> | 是 | 图片 URL 列表，**1-9** 张 |
| content | String | 否 | 图片描述，最大 **500** 字 |
| location | String | 否 | 所在位置 |
| visibility | Enum | 否 | 可见范围 |

**业务规则：**

1. 图片支持格式：JPG、PNG、WEBP、HEIC
2. 单张图片最大 **10MB**，宽度不超过 **1080px**
3. 上传时自动处理：
   - EXIF 信息提取（GPS、拍摄设备等）
   - 自动生成缩略图（300px 宽度，WebP 格式）
   - 敏感图片检测（色情、暴力、政治等）
4. 图片支持客户端裁剪、滤镜（前端处理，上传最终图片）
5. 图片展示规则：

| 图片数量 | 布局 | 说明 |
|----------|------|------|
| 1 张 | 大图模式 | 最大宽度 100%，圆角 8px |
| 2 张 | 双列布局 | 各占 50%，间距 4px |
| 3 张 | 三列布局 | 各占 33.3%，间距 4px |
| 4-6 张 | 九宫格（2行） | 每行 3 张，间距 4px |
| 7-9 张 | 九宫格（3行） | 每行 3 张，间距 4px |

#### 4.2.3 混合分享

- 支持文本 + 图片组合发布
- 图片可添加文字说明（每张图片独立描述）
- 纯文字分享时，内容居中展示，字数超过 **140** 字截断显示

#### 4.2.4 分享状态机

```
[草稿] ──提交──> [审核中] ──审核通过──> [已发布]
                       │
                       └──审核拒绝──> [已拒绝]（可重新提交）

[已发布] ──删除──> [已删除]（软删除，30天后物理清理）

[已拒绝] ──重新提交──> [审核中]
```

| 状态码 | 状态 | 说明 |
|--------|------|------|
| 0 | 草稿 | 未提交发布 |
| 1 | 审核中 | 已提交，等待审核 |
| 2 | 已发布 | 审核通过，正常展示 |
| 3 | 已拒绝 | 审核未通过 |
| 4 | 已删除 | 用户删除或违规下架 |

---

### 4.3 内容浏览模块

#### 4.3.1 好友圈信息流

**信息流排序规则：**

```
综合分 = 时间权重 × 0.3 + 互动权重 × 0.4 + 关系权重 × 0.3

时间权重 = max(0, 1 - (当前时间 - 发布时间) / 72h)  // 72小时衰减
互动权重 = (点赞数 × 1 + 评论数 × 3 + 转发数 × 5) / 100  // 简单归一化
关系权重 = 互动亲密度得分（基于历史聊天、互访频率）
```

**分页加载策略：**

- 首屏加载 **20** 条
- 下拉加载每次 **15** 条
- 支持滑动到指定时间点（如"回到昨天"）
- 缓存最近 **100** 条数据在客户端

#### 4.3.2 圈子主页

| 区域 | 内容 | 说明 |
|------|------|------|
| 顶部 | 头像、昵称、简介 | 支持编辑个人简介 |
| 统计栏 | 关注数、粉丝数、分享数 | 一键跳转关注/粉丝列表 |
| Tab 切换 | 全部 / 仅图片 / 仅视频 | 筛选不同内容类型 |
| 内容列表 | 分享动态流 | 按时间倒序，分页加载 |

#### 4.3.3 内容详情页

- 全屏展示单条分享内容
- 支持左右滑动切换分享
- 底部固定互动栏（点赞 / 评论 / 转发 / 收藏）
- 图片支持点击放大、双指缩放
- 评论区支持二级嵌套

---

### 4.4 互动模块

#### 4.4.1 点赞

| 操作 | 说明 |
|------|------|
| 点击爱心 | 切换点赞状态（支持动画效果） |
| 点赞列表 | 展示点赞用户头像 + 昵称（最近 3 个 + "等 N 人"） |
| 批量点赞 | 信息流长按支持批量点赞 |

**业务规则：**

1. 同一用户对同一分享只能点赞一次（幂等操作）
2. 取消点赞后，点赞数立即 -1
3. 点赞后发送通知给分享者（24小时内不重复通知同一人）
4. 点赞列表按时间倒序，支持分页

#### 4.4.2 评论

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| content | String | 是 | 评论内容，最大 **500** 字 |
| parentId | Long | 否 | 父评论 ID，`0` 表示一级评论 |
| replyUserId | Long | 否 | 回复的用户 ID |

**评论层级规则：**

```
一级评论（直接评论分享）
    └── 二级回复（回复一级评论）
        └── 三级回复（回复二级评论，展示为 "@用户昵称: 内容"）
```

- 一级评论最多展示 **10** 条，点击"展开全部"查看更多
- 二级回复默认展示 **3** 条，点击"查看全部 N 条回复"
- 三级及以下合并为二级回复的文字内容
- 评论支持点赞（每个评论独立计数）
- 评论支持删除（仅自己和管理员可删）

#### 4.4.3 收藏

| 操作 | 说明 |
|------|------|
| 点击收藏 | 切换收藏状态 |
| 收藏夹 | 支持创建多个收藏夹分类管理 |
| 收藏上限 | 单用户最多 **1000** 条收藏 |
| 收藏列表 | 按收藏时间倒序，支持分页 |

**业务规则：**

1. 收藏/取消收藏为幂等操作
2. 收藏夹支持重命名、删除、排序
3. 默认收藏夹名称"我的收藏"，不可删除
4. 取消收藏后，从收藏列表移除

#### 4.4.4 转发

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| content | String | 否 | 转发评论，最大 **500** 字 |
| alsoLike | Boolean | 否 | 转发时是否同时点赞，默认 `false` |

**业务规则：**

1. 转发生成新分享条目，保留原分享引用链
2. 新分享内容格式：`[转发评论]\n\n[原分享摘要]`
3. 转发后原分享者收到 `FORWARD` 通知
4. 转发次数统计实时更新
5. 支持链式转发（最多 **5** 层），超过后禁止继续转发
6. 被转发的原分享删除后，转发内容仍保留（引用标记变为"已删除"）

---

### 4.5 可见范围模块

| 范围 | 枚举值 | 说明 |
|------|--------|------|
| 公开 | `PUBLIC` | 所有用户可见（含游客） |
| 好友可见 | `FRIENDS` | 仅互相关注的好友可见（**默认**） |
| 部分可见 | `PARTIAL` | 选择特定好友可见 |
| 不给谁看 | `EXCLUDE` | 选择特定好友不可见 |
| 仅自己 | `SELF` | 仅发布者可见（私密分享） |

**可见范围交互流程：**

```
发布时选择可见范围
    │
    ├── 公开 → 信息流 + 搜索 + 话题页均可展示
    │
    ├── 好友可见 → 仅双向关注用户的信息流展示
    │
    ├── 部分可见 → 弹出好友选择器 → 多选 → 确认
    │
    ├── 不给谁看 → 弹出好友选择器 → 多选 → 确认
    │
    └── 仅自己 → 仅个人圈子可见，好友圈不展示
```

---

### 4.6 社交关系模块

#### 4.6.1 关注/粉丝体系

| 操作 | 说明 |
|------|------|
| 关注 | 单向关注，关注后可在好友圈看到对方动态 |
| 互关 | 双向关注，成为好友关系 |
| 取消关注 | 取消后对方动态不再出现在好友圈 |

**好友关系判定：**

```
用户A 关注了用户B 且 用户B 关注了用户A
    → A 和 B 互为好友
    → 彼此的非公开分享可见
```

#### 4.6.2 关注/粉丝列表

- 关注列表按关注时间倒序
- 粉丝列表按关注时间倒序
- 支持搜索（按昵称模糊匹配）
- 每个用户项展示：头像、昵称、简介、互关状态

---

## 5. 业务规则与约束

### 5.1 内容限制

| 规则 | 限制值 | 说明 |
|------|--------|------|
| 文本最大长度 | 1000 字 | 超过截断并提示 |
| 图片最大数量 | 9 张 | 超过拒绝并提示 |
| 单张图片大小 | 10 MB | 超过自动压缩或拒绝 |
| 话题标签数量 | 5 个 | 超过忽略多余标签 |
| @好友数量 | 10 人 | 超过忽略多余 @ |

### 5.2 频率限制（防刷机制）

| 操作 | 限制 | 触发后处理 |
|------|------|------------|
| 发布分享 | **10 条/小时** | 返回 429，提示"操作太频繁" |
| 点赞 | **100 次/小时** | 返回 429，提示"操作太频繁" |
| 评论 | **50 条/小时** | 返回 429，提示"操作太频繁" |
| 转发 | **20 次/小时** | 返回 429，提示"操作太频繁" |
| 关注 | **50 人/天** | 返回 429，提示"今日关注已达上限" |
| 图片上传 | **100 张/小时** | 返回 429，提示"上传太频繁" |

### 5.3 内容安全

- **文本过滤：** 阿里云内容安全 API + 本地敏感词库（双层过滤）
- **图片审核：** 阿里云图片鉴黄 + OCR 文字识别（防水印广告）
- **审核策略：**

| 审核方式 | 触发条件 | 处理时效 |
|----------|----------|----------|
| 机审 | 所有新发布内容 | 实时（< 3s） |
| 人审 | 机审置信度 0.5-0.8 | 24 小时内 |
| 用户举报 | 被举报内容 | 4 小时内处理 |

### 5.4 缓存策略

| 数据类型 | 缓存方式 | 过期时间 |
|----------|----------|----------|
| 好友圈信息流 | Redis ZSet | 10 分钟 |
| 分享详情 | Redis Hash | 30 分钟 |
| 点赞状态 | Redis Set | 1 小时 |
| 收藏状态 | Redis Set | 1 小时 |
| 用户关注关系 | Redis Set | 永不过期（写时更新） |
| 未读通知数 | Redis String | 永不过期（写时更新） |

---

## 6. 数据库设计

### 6.1 ER 关系图

```
┌──────────┐     1:N     ┌──────────────┐
│  share   │────────────>│ share_image  │
└────┬─────┘             └──────────────┘
     │
     ├── 1:N ──> share_like
     │
     ├── 1:N ──> share_comment（自关联，parent_id）
     │
     ├── 1:N ──> share_collect
     │
     ├── 1:N ──> share_forward
     │
     └── 1:N ──> notification

┌──────────────┐
│  friendship  │（自关联表，user_id + friend_id）
└──────────────┘
```

### 6.2 表结构定义

#### 6.2.1 share（分享表）

```sql
CREATE TABLE `share` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT COMMENT '分享ID',
    `user_id`       BIGINT          NOT NULL COMMENT '发布者ID',
    `content`       TEXT            DEFAULT NULL COMMENT '文本内容',
    `location`      VARCHAR(255)    DEFAULT NULL COMMENT '所在位置',
    `visibility`    TINYINT         NOT NULL DEFAULT 2 COMMENT '可见范围: 1公开 2好友 3部分可见 4不给谁看 5仅自己',
    `like_count`    INT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '点赞数',
    `comment_count` INT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '评论数',
    `forward_count` INT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '转发数',
    `collect_count` INT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '收藏数',
    `status`        TINYINT         NOT NULL DEFAULT 2 COMMENT '状态: 0草稿 1审核中 2已发布 3已拒绝 4已删除',
    `deleted`       TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0否 1是',
    `create_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_create_time` (`create_time`),
    INDEX `idx_status` (`status`),
    INDEX `idx_user_status` (`user_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分享表';
```

#### 6.2.2 share_image（分享图片表）

```sql
CREATE TABLE `share_image` (
    `id`         BIGINT          NOT NULL AUTO_INCREMENT COMMENT '图片ID',
    `share_id`   BIGINT          NOT NULL COMMENT '分享ID',
    `image_url`  VARCHAR(500)    NOT NULL COMMENT '原图URL',
    `thumb_url`  VARCHAR(500)    DEFAULT NULL COMMENT '缩略图URL',
    `width`      INT UNSIGNED    DEFAULT NULL COMMENT '图片宽度(px)',
    `height`     INT UNSIGNED    DEFAULT NULL COMMENT '图片高度(px)',
    `file_size`  BIGINT UNSIGNED DEFAULT NULL COMMENT '文件大小(字节)',
    `sort_order` INT             NOT NULL DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_share_id` (`share_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分享图片表';
```

#### 6.2.3 share_like（点赞表）

```sql
CREATE TABLE `share_like` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
    `share_id`    BIGINT      NOT NULL COMMENT '分享ID',
    `user_id`     BIGINT      NOT NULL COMMENT '用户ID',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_share_user` (`share_id`, `user_id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='点赞表';
```

#### 6.2.4 share_comment（评论表）

```sql
CREATE TABLE `share_comment` (
    `id`             BIGINT          NOT NULL AUTO_INCREMENT COMMENT '评论ID',
    `share_id`       BIGINT          NOT NULL COMMENT '分享ID',
    `user_id`        BIGINT          NOT NULL COMMENT '评论者ID',
    `parent_id`      BIGINT          NOT NULL DEFAULT 0 COMMENT '父评论ID（0=一级评论）',
    `reply_user_id`  BIGINT          NOT NULL DEFAULT 0 COMMENT '回复的用户ID',
    `content`        VARCHAR(500)    NOT NULL COMMENT '评论内容',
    `like_count`     INT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '点赞数',
    `status`         TINYINT         NOT NULL DEFAULT 1 COMMENT '状态: 0删除 1正常',
    `deleted`        TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0否 1是',
    `create_time`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_share_id` (`share_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';
```

#### 6.2.5 share_collect（收藏表）

```sql
CREATE TABLE `share_collect` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
    `share_id`    BIGINT      NOT NULL COMMENT '分享ID',
    `user_id`     BIGINT      NOT NULL COMMENT '用户ID',
    `folder_id`   BIGINT      NOT NULL DEFAULT 0 COMMENT '收藏夹ID（0=默认收藏夹）',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_share_user` (`share_id`, `user_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_folder_id` (`folder_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';
```

#### 6.2.6 share_forward（转发表）

```sql
CREATE TABLE `share_forward` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT COMMENT '转发ID',
    `share_id`      BIGINT          NOT NULL COMMENT '原分享ID',
    `user_id`       BIGINT          NOT NULL COMMENT '转发者ID',
    `content`       VARCHAR(500)    DEFAULT NULL COMMENT '转发评论',
    `new_share_id`  BIGINT          DEFAULT NULL COMMENT '转发后新生成的分享ID',
    `create_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_share_id` (`share_id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='转发表';
```

#### 6.2.7 friendship（好友关系表）

```sql
CREATE TABLE `friendship` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '关系ID',
    `user_id`     BIGINT      NOT NULL COMMENT '用户ID',
    `friend_id`   BIGINT      NOT NULL COMMENT '好友ID',
    `status`      TINYINT     NOT NULL DEFAULT 1 COMMENT '状态: 0已删除 1已关注 2已互关',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_friend` (`user_id`, `friend_id`),
    INDEX `idx_friend_id` (`friend_id`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='好友关系表';
```

#### 6.2.8 notification（通知表）

```sql
CREATE TABLE `notification` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT COMMENT '通知ID',
    `user_id`       BIGINT          NOT NULL COMMENT '接收者ID',
    `type`          VARCHAR(20)     NOT NULL COMMENT '通知类型: LIKE/COMMENT/FORWARD/FOLLOW/MENTION',
    `sender_id`     BIGINT          NOT NULL COMMENT '发送者ID',
    `related_id`    BIGINT          NOT NULL COMMENT '关联内容ID',
    `content`       VARCHAR(500)    DEFAULT NULL COMMENT '通知内容摘要',
    `is_read`       TINYINT         NOT NULL DEFAULT 0 COMMENT '是否已读: 0未读 1已读',
    `create_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_user_read` (`user_id`, `is_read`),
    INDEX `idx_type` (`type`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知表';
```

#### 6.2.9 share_visibility_detail（可见范围明细表）

```sql
CREATE TABLE `share_visibility_detail` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `share_id`    BIGINT      NOT NULL COMMENT '分享ID',
    `user_id`     BIGINT      NOT NULL COMMENT '可见/不可见的用户ID',
    `type`        TINYINT     NOT NULL COMMENT '类型: 1可见 2不可见',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_share_user` (`share_id`, `user_id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='可见范围明细表';
```

---

## 7. API 接口设计

### 7.1 接口规范

| 规范项 | 说明 |
|--------|------|
| 协议 | HTTPS |
| 数据格式 | JSON |
| 认证方式 | Bearer Token（JWT） |
| 分页参数 | `pageNum`（从1开始）、`pageSize`（默认10，最大50） |
| 返回格式 | `{ "code": 200, "message": "success", "data": {} }` |
| 错误码 | 见附录 B |

### 7.2 分享接口

#### 7.2.1 发布分享

```http
POST /api/v1/share
Authorization: Bearer <token>
Content-Type: application/json

Request Body:
{
    "content": "今天天气真好！",
    "imageUrls": ["https://oss.xxx.com/img/1.jpg", "https://oss.xxx.com/img/2.jpg"],
    "location": "北京市朝阳区",
    "visibility": 2,
    "mentionUserIds": [123, 456],
    "topicTags": ["天气", "心情"]
}

Response:
{
    "code": 200,
    "message": "success",
    "data": {
        "shareId": 1001,
        "content": "今天天气真好！",
        "images": [
            { "imageUrl": "...", "thumbUrl": "...", "width": 1080, "height": 720 }
        ],
        "location": "北京市朝阳区",
        "visibility": 2,
        "createTime": "2026-05-09T14:00:00"
    }
}
```

#### 7.2.2 获取分享详情

```http
GET /api/v1/share/{shareId}
Authorization: Bearer <token>

Response:
{
    "code": 200,
    "message": "success",
    "data": {
        "shareId": 1001,
        "user": {
            "userId": 1,
            "nickname": "用户A",
            "avatar": "https://oss.xxx.com/avatar/1.jpg"
        },
        "content": "今天天气真好！",
        "images": [...],
        "location": "北京市朝阳区",
        "likeCount": 10,
        "commentCount": 5,
        "forwardCount": 2,
        "collectCount": 3,
        "isLiked": false,
        "isCollected": false,
        "createTime": "2026-05-09T14:00:00"
    }
}
```

#### 7.2.3 获取我的分享列表

```http
GET /api/v1/share/my?pageNum=1&pageSize=10
Authorization: Bearer <token>
```

#### 7.2.4 获取好友圈动态

```http
GET /api/v1/share/friends?pageNum=1&pageSize=10
Authorization: Bearer <token>
```

#### 7.2.5 获取用户圈子

```http
GET /api/v1/share/user/{userId}?pageNum=1&pageSize=10
Authorization: Bearer <token>
```

#### 7.2.6 删除分享

```http
DELETE /api/v1/share/{shareId}
Authorization: Bearer <token>
```

### 7.3 互动接口

#### 7.3.1 点赞/取消点赞

```http
POST /api/v1/share/{shareId}/like
Authorization: Bearer <token>

Response:
{
    "code": 200,
    "data": {
        "isLiked": true,
        "likeCount": 11
    }
}
```

#### 7.3.2 获取点赞列表

```http
GET /api/v1/share/{shareId}/likes?pageNum=1&pageSize=20
Authorization: Bearer <token>
```

#### 7.3.3 收藏/取消收藏

```http
POST /api/v1/share/{shareId}/collect
Authorization: Bearer <token>

Response:
{
    "code": 200,
    "data": {
        "isCollected": true,
        "collectCount": 5
    }
}
```

#### 7.3.4 获取收藏列表

```http
GET /api/v1/share/collects?pageNum=1&pageSize=10
Authorization: Bearer <token>
```

#### 7.3.5 转发分享

```http
POST /api/v1/share/{shareId}/forward
Authorization: Bearer <token>
Content-Type: application/json

Request Body:
{
    "content": "推荐给大家！",
    "alsoLike": true
}

Response:
{
    "code": 200,
    "data": {
        "forwardId": 201,
        "newShareId": 1002,
        "forwardCount": 3
    }
}
```

### 7.4 评论接口

#### 7.4.1 发表评论

```http
POST /api/v1/share/{shareId}/comment
Authorization: Bearer <token>
Content-Type: application/json

Request Body:
{
    "content": "拍得真好看！",
    "parentId": 0,
    "replyUserId": 0
}
```

#### 7.4.2 获取评论列表

```http
GET /api/v1/share/{shareId}/comments?pageNum=1&pageSize=20
Authorization: Bearer <token>
```

#### 7.4.3 删除评论

```http
DELETE /api/v1/share/{shareId}/comment/{commentId}
Authorization: Bearer <token>
```

#### 7.4.4 评论点赞

```http
POST /api/v1/share/comment/{commentId}/like
Authorization: Bearer <token>
```

### 7.5 社交关系接口

#### 7.5.1 关注用户

```http
POST /api/v1/friend/follow/{userId}
Authorization: Bearer <token>
```

#### 7.5.2 取消关注

```http
DELETE /api/v1/friend/follow/{userId}
Authorization: Bearer <token>
```

#### 7.5.3 获取关注列表

```http
GET /api/v1/friend/following?pageNum=1&pageSize=20
Authorization: Bearer <token>
```

#### 7.5.4 获取粉丝列表

```http
GET /api/v1/friend/followers?pageNum=1&pageSize=20
Authorization: Bearer <token>
```

#### 7.5.5 获取好友列表

```http
GET /api/v1/friend/friends?pageNum=1&pageSize=20
Authorization: Bearer <token>
```

### 7.6 通知接口

#### 7.6.1 获取通知列表

```http
GET /api/v1/notification/list?pageNum=1&pageSize=20&type=LIKE
Authorization: Bearer <token>
```

#### 7.6.2 获取未读数量

```http
GET /api/v1/notification/unread
Authorization: Bearer <token>

Response:
{
    "code": 200,
    "data": {
        "total": 12,
        "like": 5,
        "comment": 3,
        "forward": 2,
        "follow": 1,
        "mention": 1
    }
}
```

#### 7.6.3 标记已读

```http
PUT /api/v1/notification/{notificationId}/read
Authorization: Bearer <token>
```

#### 7.6.4 全部已读

```http
PUT /api/v1/notification/read-all?type=LIKE
Authorization: Bearer <token>
```

### 7.7 图片上传接口

```http
POST /api/v1/share/image/upload
Authorization: Bearer <token>
Content-Type: multipart/form-data

Form Data:
    file: <二进制文件>

Response:
{
    "code": 200,
    "data": {
        "imageUrl": "https://oss.xxx.com/share/abc123.jpg",
        "thumbUrl": "https://oss.xxx.com/share/abc123_thumb.webp",
        "width": 1080,
        "height": 720,
        "fileSize": 524288
    }
}
```

---

## 8. 通知体系

### 8.1 通知类型

| 类型 | 枚举值 | 触发场景 | 通知内容示例 |
|------|--------|----------|--------------|
| 点赞 | `LIKE` | 好友点赞你的分享 | "用户B 赞了你的分享" |
| 评论 | `COMMENT` | 好友评论你的分享 | "用户B 评论了你的分享" |
| 回复 | `REPLY` | 好友回复你的评论 | "用户B 回复了你的评论" |
| 转发 | `FORWARD` | 好友转发你的分享 | "用户B 转发了你的分享" |
| 关注 | `FOLLOW` | 用户关注了你 | "用户B 关注了你" |
| @提及 | `MENTION` | 好友在分享中@你 | "用户B 在分享中提到了你" |

### 8.2 通知合并策略

| 场景 | 合并规则 | 展示形式 |
|------|----------|----------|
| 多人点赞 | 同一分享 10 分钟内多人点赞 | "用户A、用户B 等 5 人赞了你的分享" |
| 多人评论 | 同一分享 30 分钟内多人评论 | "用户A 等 3 人评论了你的分享" |
| 批量关注 | 1 小时内多人关注 | "用户A 等 5 人关注了你" |

### 8.3 推送策略

| 通知类型 | 站内通知 | APP 推送 | 短信通知 |
|----------|----------|----------|----------|
| 点赞 | ✅ | ❌（批量推送，每小时汇总） | ❌ |
| 评论 | ✅ | ✅（实时） | ❌ |
| 转发 | ✅ | ❌ | ❌ |
| 关注 | ✅ | ✅（实时） | ❌ |
| @提及 | ✅ | ✅（实时） | ❌ |

---

## 9. 内容安全与审核

### 9.1 审核流程

```
用户发布内容
    │
    ▼
[机审] ──通过──> [直接发布]
    │
    └──疑似违规──> [进入人工审核队列] ──通过──> [发布]
                           │
                           └──违规──> [拒绝发布 + 通知用户]
```

### 9.2 举报机制

```http
POST /api/v1/share/{shareId}/report
Authorization: Bearer <token>
Content-Type: application/json

Request Body:
{
    "reason": "SPAM",
    "description": "疑似广告内容"
}
```

| 举报原因 | 枚举值 | 处理优先级 |
|----------|--------|------------|
| 色情低俗 | `PORNOGRAPHY` | P0（立即处理） |
| 暴力血腥 | `VIOLENCE` | P0（立即处理） |
| 政治敏感 | `POLITICAL` | P0（立即处理） |
| 垃圾广告 | `SPAM` | P1（24小时内） |
| 诈骗信息 | `SCAM` | P1（24小时内） |
| 侵权内容 | `INFRINGEMENT` | P2（48小时内） |
| 其他 | `OTHER` | P2（48小时内） |

### 9.3 处罚机制

| 违规次数 | 处罚措施 |
|----------|----------|
| 第 1 次 | 内容下架 + 警告通知 |
| 第 2 次 | 禁言 3 天 + 内容下架 |
| 第 3 次 | 禁言 7 天 + 内容下架 |
| 第 4 次 | 永久封号 |

---

## 10. 性能与技术指标

### 10.1 接口性能

| 接口 | 目标响应时间 | P99 延迟 |
|------|-------------|----------|
| 发布分享 | < 500ms | < 1s |
| 信息流加载 | < 1s | < 2s |
| 点赞/取消点赞 | < 200ms | < 500ms |
| 评论发布 | < 300ms | < 800ms |
| 图片上传 | 支持断点续传 | — |

### 10.2 并发指标

| 指标 | 目标值 |
|------|--------|
| 信息流 QPS | ≥ 5,000 |
| 点赞 QPS | ≥ 10,000 |
| 评论 QPS | ≥ 3,000 |
| 图片上传 QPS | ≥ 500 |

### 10.3 可用性

| 指标 | 目标值 |
|------|--------|
| 系统可用性 | ≥ 99.9% |
| 数据持久化 | 双写 MySQL + Redis |
| 故障恢复 | RTO < 5 分钟，RPO < 1 分钟 |

---

## 11. 版本规划与里程碑

### v1.0（MVP）—— 核心发布 + 基础互动

- [ ] 文本分享发布
- [ ] 图片分享（最多 9 张）
- [ ] 可见范围（公开/好友/仅自己）
- [ ] 点赞、评论功能
- [ ] 好友圈信息流
- [ ] 个人圈子主页
- [ ] 通知推送（站内）

**预计工期：** 3 周

### v1.1 —— 互动增强

- [ ] 转发功能
- [ ] 收藏功能（含收藏夹管理）
- [ ] 关注/粉丝体系
- [ ] 二级评论回复
- [ ] 通知合并策略
- [ ] APP 推送集成

**预计工期：** 2 周

### v1.2 —— 安全与运营

- [ ] 内容审核系统（机审 + 人审）
- [ ] 举报机制
- [ ] 话题标签聚合页
- [ ] 内容分享统计看板
- [ ] 搜索功能（按内容/用户/话题）

**预计工期：** 2 周

### v2.0 —— 内容升级

- [ ] 视频分享
- [ ] 图片编辑器（滤镜、贴纸、文字）
- [ ] 信息流算法推荐
- [ ] 内容热榜
- [ ] 直播功能

**预计工期：** 4 周

---

## 附录 A：状态机定义

### 分享状态流转

```
        ┌─────────┐
        │  草稿   │
        └────┬────┘
             │ 提交
             ▼
        ┌─────────┐
        │ 审核中  │
        └────┬────┘
       ┌─────┴─────┐
       │           │
       ▼           ▼
  ┌─────────┐ ┌─────────┐
  │ 已发布  │ │ 已拒绝  │
  └────┬────┘ └────┬────┘
       │           │
       │ 删除      │ 重新提交
       ▼           │
  ┌─────────┐      │
  │ 已删除  │      │
  └─────────┘      │
                   └──> 审核中
```

### 好友关系状态流转

```
[无关系] ──关注──> [已关注（单向）] ──对方也关注──> [已互关]
     │                    │
     └────────────────────┘ 取消关注
```

---

## 附录 B：异常码规范

| 异常码 | HTTP 状态码 | 说明 |
|--------|-------------|------|
| 10001 | 400 | 请求参数校验失败 |
| 10002 | 401 | Token 无效或过期 |
| 10003 | 403 | 无权限访问 |
| 10004 | 404 | 资源不存在 |
| 20001 | 400 | 分享内容为空 |
| 20002 | 400 | 图片数量超过限制 |
| 20003 | 400 | 图片大小超过限制 |
| 20004 | 403 | 无权查看该分享 |
| 20005 | 403 | 分享已被删除 |
| 20006 | 429 | 发布频率超限 |
| 20007 | 400 | 内容包含敏感词 |
| 20008 | 400 | 图片审核未通过 |
| 30001 | 429 | 点赞频率超限 |
| 30002 | 400 | 评论内容为空 |
| 30003 | 429 | 评论频率超限 |
| 30004 | 400 | 评论层级过深 |
| 40001 | 400 | 已是好友关系 |
| 40002 | 400 | 未关注该用户 |
| 40003 | 429 | 关注频率超限 |
| 50001 | 400 | 收藏夹已满 |
| 50002 | 400 | 转发层级超过限制 |

---

> **文档审批记录**
>
> | 角色 | 姓名 | 审批日期 | 备注 |
> |------|------|----------|------|
> | 产品经理 | — | — | — |
> | 技术负责人 | — | — | — |
> | 测试负责人 | — | — | — |
> | 运营负责人 | — | — | — |
