# 分享功能需求文档

## 1. 功能概述

分享功能允许用户将文本、图片内容发布到自己的圈子，好友可以对分享内容进行点赞、收藏、转发等互动操作。

### 1.1 功能目标

- 支持文本、图片多种形式的内容分享
- 建立用户圈子体系，实现内容定向推送
- 提供丰富的互动功能，增强用户粘性

### 1.2 用户角色

| 角色 | 说明 |
|------|------|
| 分享者 | 发布分享内容的用户 |
| 浏览者 | 查看分享内容的用户（好友） |
| 管理员 | 管理分享内容、处理举报 |

---

## 2. 功能详情

### 2.1 分享发布

#### 2.1.1 文本分享

- **输入项**
  - 文本内容（必填，最大 1000 字）
  - 所在位置（选填）
  - 可见范围（选填，默认：所有好友）

- **业务规则**
  - 文本内容需过滤敏感词
  - 支持 @ 好友功能
  - 支持话题标签 #xxx#

#### 2.1.2 图片分享

- **输入项**
  - 图片（必填，最多 9 张）
  - 图片描述（选填，最大 500 字）
  - 所在位置（选填）
  - 可见范围（选填，默认：所有好友）

- **业务规则**
  - 支持图片裁剪、滤镜
  - 图片自动压缩（宽度不超过 1080px）
  - 生成缩略图用于列表展示

#### 2.1.3 混合分享

- 支持文本 + 图片组合发布
- 图片可添加文字说明

### 2.2 可见范围

| 范围 | 说明 |
|------|------|
| 公开 | 所有用户可见 |
| 好友可见 | 仅互相关注的好友可见 |
| 部分可见 | 选择特定好友可见 |
| 不给谁看 | 选择特定好友不可见 |
| 仅自己可见 | 仅发布者可见 |

---

## 3. 圈子功能

### 3.1 圈子定义

圈子是用户内容的聚合空间，用户发布的所有分享都会展示在自己的圈子中。

### 3.2 圈子动态流

- **我的圈子**：展示自己发布的所有分享
- **好友圈**：展示好友的分享动态
- **热门圈**：展示平台热门分享内容

### 3.3 圈子主页

- 用户头像、昵称、简介
- 分享内容列表（分页加载）
- 关注/粉丝数量
- 分享总数

---

## 4. 互动功能

### 4.1 点赞

- **操作说明**
  - 点击爱心图标进行点赞/取消点赞
  - 支持点赞动画效果
  - 实时更新点赞数量

- **业务规则**
  - 同一用户对同一分享只能点赞一次
  - 点赞后发送通知给分享者
  - 点赞记录可查询

### 4.2 收藏

- **操作说明**
  - 点击收藏图标进行收藏/取消收藏
  - 收藏内容可在"我的收藏"查看

- **业务规则**
  - 同一用户对同一分享只能收藏一次
  - 收藏夹支持分类管理
  - 收藏数量有上限（默认 1000）

### 4.3 转发

- **操作说明**
  - 点击转发按钮，可添加转发评论
  - 转发内容展示原始分享 + 评论
  - 支持转发到自己的圈子

- **业务规则**
  - 转发时可选择是否同时点赞
  - 转发内容保留原分享引用链
  - 原分享者收到转发通知

### 4.4 评论

- **操作说明**
  - 支持文字评论
  - 支持回复评论（二级评论）
  - 支持评论点赞

- **业务规则**
  - 评论内容需过滤敏感词
  - 单条分享评论上限 1000 条
  - 支持删除自己的评论

---

## 5. 数据库设计

### 5.1 分享表 (share)

```sql
CREATE TABLE share (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分享ID',
    user_id BIGINT NOT NULL COMMENT '发布者ID',
    content TEXT COMMENT '文本内容',
    location VARCHAR(255) COMMENT '所在位置',
    visibility TINYINT DEFAULT 1 COMMENT '可见范围: 1公开 2好友 3部分可见 4不给谁看 5仅自己',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    comment_count INT DEFAULT 0 COMMENT '评论数',
    forward_count INT DEFAULT 0 COMMENT '转发数',
    collect_count INT DEFAULT 0 COMMENT '收藏数',
    status TINYINT DEFAULT 1 COMMENT '状态: 0删除 1正常 2审核中',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分享表';
```

### 5.2 分享图片表 (share_image)

```sql
CREATE TABLE share_image (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '图片ID',
    share_id BIGINT NOT NULL COMMENT '分享ID',
    image_url VARCHAR(500) NOT NULL COMMENT '原图URL',
    thumb_url VARCHAR(500) COMMENT '缩略图URL',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_share_id (share_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分享图片表';
```

### 5.3 点赞表 (share_like)

```sql
CREATE TABLE share_like (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '点赞ID',
    share_id BIGINT NOT NULL COMMENT '分享ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_share_user (share_id, user_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞表';
```

### 5.4 收藏表 (share_collect)

```sql
CREATE TABLE share_collect (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '收藏ID',
    share_id BIGINT NOT NULL COMMENT '分享ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    folder_id BIGINT DEFAULT 0 COMMENT '收藏夹ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_share_user (share_id, user_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';
```

### 5.5 转发表 (share_forward)

```sql
CREATE TABLE share_forward (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '转发ID',
    share_id BIGINT NOT NULL COMMENT '原分享ID',
    user_id BIGINT NOT NULL COMMENT '转发者ID',
    content VARCHAR(500) COMMENT '转发评论',
    new_share_id BIGINT COMMENT '新分享ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_share_id (share_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='转发表';
```

### 5.6 评论表 (share_comment)

```sql
CREATE TABLE share_comment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
    share_id BIGINT NOT NULL COMMENT '分享ID',
    user_id BIGINT NOT NULL COMMENT '评论者ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父评论ID',
    reply_user_id BIGINT DEFAULT 0 COMMENT '回复用户ID',
    content VARCHAR(500) NOT NULL COMMENT '评论内容',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    status TINYINT DEFAULT 1 COMMENT '状态: 0删除 1正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_share_id (share_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';
```

### 5.7 好友关系表 (friendship)

```sql
CREATE TABLE friendship (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关系ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    friend_id BIGINT NOT NULL COMMENT '好友ID',
    status TINYINT DEFAULT 1 COMMENT '状态: 0已删除 1已关注 2已互关',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_friend (user_id, friend_id),
    INDEX idx_friend_id (friend_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='好友关系表';
```

---

## 6. API 接口设计

### 6.1 分享接口

#### 发布分享
```
POST /share
Authorization: Bearer <token>
Content-Type: application/json

Request:
{
    "content": "今天天气真好！",
    "imageUrls": ["url1.jpg", "url2.jpg"],
    "location": "北京市朝阳区",
    "visibility": 1,
    "mentionUserIds": [123, 456]
}

Response:
{
    "code": 200,
    "data": {
        "shareId": 1001,
        "content": "今天天气真好！",
        "images": [...],
        "createTime": "2026-05-08 10:30:00"
    }
}
```

#### 获取分享详情
```
GET /share/{shareId}
Authorization: Bearer <token>

Response:
{
    "code": 200,
    "data": {
        "shareId": 1001,
        "user": {
            "userId": 1,
            "nickname": "用户A",
            "avatar": "url"
        },
        "content": "今天天气真好！",
        "images": [...],
        "location": "北京市朝阳区",
        "likeCount": 10,
        "commentCount": 5,
        "forwardCount": 2,
        "isLiked": false,
        "isCollected": false,
        "createTime": "2026-05-08 10:30:00"
    }
}
```

#### 获取我的分享列表
```
GET /share/my?pageNum=1&pageSize=10
Authorization: Bearer <token>

Response:
{
    "code": 200,
    "data": {
        "records": [...],
        "total": 50,
        "pages": 5,
        "current": 1
    }
}
```

#### 获取好友圈动态
```
GET /share/friends?pageNum=1&pageSize=10
Authorization: Bearer <token>

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

#### 删除分享
```
DELETE /share/{shareId}
Authorization: Bearer <token>

Response:
{
    "code": 200,
    "message": "删除成功"
}
```

---

### 6.2 点赞接口

#### 点赞/取消点赞
```
POST /share/{shareId}/like
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

#### 获取点赞列表
```
GET /share/{shareId}/likes?pageNum=1&pageSize=20
Authorization: Bearer <token>

Response:
{
    "code": 200,
    "data": {
        "records": [
            {
                "userId": 2,
                "nickname": "用户B",
                "avatar": "url",
                "likeTime": "2026-05-08 11:00:00"
            }
        ],
        "total": 10
    }
}
```

---

### 6.3 收藏接口

#### 收藏/取消收藏
```
POST /share/{shareId}/collect
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

#### 获取我的收藏列表
```
GET /share/collects?pageNum=1&pageSize=10
Authorization: Bearer <token>

Response:
{
    "code": 200,
    "data": {
        "records": [...],
        "total": 30,
        "pages": 3,
        "current": 1
    }
}
```

---

### 6.4 转发接口

#### 转发分享
```
POST /share/{shareId}/forward
Authorization: Bearer <token>
Content-Type: application/json

Request:
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

---

### 6.5 评论接口

#### 发表评论
```
POST /share/{shareId}/comment
Authorization: Bearer <token>
Content-Type: application/json

Request:
{
    "content": "拍得真好看！",
    "parentId": 0,
    "replyUserId": 0
}

Response:
{
    "code": 200,
    "data": {
        "commentId": 301,
        "content": "拍得真好看！",
        "createTime": "2026-05-08 12:00:00"
    }
}
```

#### 获取评论列表
```
GET /share/{shareId}/comments?pageNum=1&pageSize=20
Authorization: Bearer <token>

Response:
{
    "code": 200,
    "data": {
        "records": [
            {
                "commentId": 301,
                "user": {
                    "userId": 3,
                    "nickname": "用户C",
                    "avatar": "url"
                },
                "content": "拍得真好看！",
                "likeCount": 2,
                "isLiked": false,
                "replyList": [...],
                "createTime": "2026-05-08 12:00:00"
            }
        ],
        "total": 5
    }
}
```

#### 删除评论
```
DELETE /share/{shareId}/comment/{commentId}
Authorization: Bearer <token>

Response:
{
    "code": 200,
    "message": "删除成功"
}
```

---

### 6.6 好友接口

#### 关注用户
```
POST /friend/follow/{userId}
Authorization: Bearer <token>

Response:
{
    "code": 200,
    "data": {
        "isMutual": false
    }
}
```

#### 取消关注
```
DELETE /friend/follow/{userId}
Authorization: Bearer <token>

Response:
{
    "code": 200,
    "message": "取消关注成功"
}
```

#### 获取关注列表
```
GET /friend/following?pageNum=1&pageSize=20
Authorization: Bearer <token>

Response:
{
    "code": 200,
    "data": {
        "records": [...],
        "total": 50
    }
}
```

#### 获取粉丝列表
```
GET /friend/followers?pageNum=1&pageSize=20
Authorization: Bearer <token>

Response:
{
    "code": 200,
    "data": {
        "records": [...],
        "total": 30
    }
}
```

#### 获取好友列表（互相关注）
```
GET /friend/friends?pageNum=1&pageSize=20
Authorization: Bearer <token>

Response:
{
    "code": 200,
    "data": {
        "records": [...],
        "total": 20
    }
}
```

---

## 7. 通知机制

### 7.1 通知类型

| 类型 | 触发场景 |
|------|----------|
| LIKE | 好友点赞你的分享 |
| COMMENT | 好友评论你的分享 |
| FORWARD | 好友转发你的分享 |
| FOLLOW | 用户关注了你 |
| MENTION | 好友在分享中@你 |

### 7.2 通知接口

#### 获取通知列表
```
GET /notification/list?pageNum=1&pageSize=20&type=LIKE
Authorization: Bearer <token>

Response:
{
    "code": 200,
    "data": {
        "records": [
            {
                "notificationId": 1,
                "type": "LIKE",
                "content": "用户B 赞了你的分享",
                "relatedId": 1001,
                "isRead": false,
                "createTime": "2026-05-08 10:30:00"
            }
        ],
        "total": 100,
        "unreadCount": 5
    }
}
```

#### 标记通知已读
```
PUT /notification/{notificationId}/read
Authorization: Bearer <token>

Response:
{
    "code": 200,
    "message": "标记成功"
}
```

#### 标记所有通知已读
```
PUT /notification/read-all
Authorization: Bearer <token>

Response:
{
    "code": 200,
    "message": "标记成功"
}
```

---

## 8. 安全与审核

### 8.1 内容审核

- 文本内容敏感词过滤
- 图片内容违规检测
- 人工审核队列

### 8.2 举报机制

#### 举报分享
```
POST /share/{shareId}/report
Authorization: Bearer <token>
Content-Type: application/json

Request:
{
    "reason": "违规内容",
    "description": "包含色情信息"
}

Response:
{
    "code": 200,
    "message": "举报成功"
}
```

### 8.3 频率限制

| 操作 | 限制 |
|------|------|
| 发布分享 | 10条/小时 |
| 点赞 | 100次/小时 |
| 评论 | 50条/小时 |
| 转发 | 20次/小时 |

---

## 9. 性能要求

| 指标 | 要求 |
|------|------|
| 分享发布接口响应时间 | < 500ms |
| 好友圈加载时间 | < 1s |
| 点赞实时更新 | < 200ms |
| 图片上传 | 支持断点续传 |

---

## 10. 版本规划

### v1.0 (MVP)

- 基础文本分享
- 图片分享（最多9张）
- 点赞、评论功能
- 好友圈动态流

### v1.1

- 转发功能
- 收藏功能
- 通知系统

### v1.2

- 圈子分组
- 内容审核增强
- 数据统计分析

### v2.0

- 视频分享
- 直播功能
- 话题广场
