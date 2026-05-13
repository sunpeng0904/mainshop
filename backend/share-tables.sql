-- 分享功能相关表结构

-- 分享表
CREATE TABLE IF NOT EXISTS `share` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分享ID',
    `user_id` BIGINT NOT NULL COMMENT '发布者ID',
    `content` TEXT COMMENT '文本内容',
    `location` VARCHAR(255) COMMENT '所在位置',
    `visibility` TINYINT DEFAULT 1 COMMENT '可见范围: 1公开 2好友 3部分可见 4不给谁看 5仅自己',
    `like_count` INT DEFAULT 0 COMMENT '点赞数',
    `comment_count` INT DEFAULT 0 COMMENT '评论数',
    `forward_count` INT DEFAULT 0 COMMENT '转发数',
    `collect_count` INT DEFAULT 0 COMMENT '收藏数',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0删除 1正常 2审核中',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` INT DEFAULT 0 COMMENT '逻辑删除',
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_create_time` (`create_time`),
    INDEX `idx_user_time` (`user_id`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分享表';

-- 分享图片表
CREATE TABLE IF NOT EXISTS `share_image` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '图片ID',
    `share_id` BIGINT NOT NULL COMMENT '分享ID',
    `image_url` VARCHAR(500) NOT NULL COMMENT '原图URL',
    `thumb_url` VARCHAR(500) COMMENT '缩略图URL',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_share_id` (`share_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分享图片表';

-- 点赞表
CREATE TABLE IF NOT EXISTS `share_like` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '点赞ID',
    `share_id` BIGINT NOT NULL COMMENT '分享ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_share_user` (`share_id`, `user_id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞表';

-- 收藏表
CREATE TABLE IF NOT EXISTS `share_collect` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '收藏ID',
    `share_id` BIGINT NOT NULL COMMENT '分享ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `folder_id` BIGINT DEFAULT 0 COMMENT '收藏夹ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_share_user` (`share_id`, `user_id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- 转发表
CREATE TABLE IF NOT EXISTS `share_forward` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '转发ID',
    `share_id` BIGINT NOT NULL COMMENT '原分享ID',
    `user_id` BIGINT NOT NULL COMMENT '转发者ID',
    `content` VARCHAR(500) COMMENT '转发评论',
    `new_share_id` BIGINT COMMENT '新分享ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_share_id` (`share_id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='转发表';

-- 评论表
CREATE TABLE IF NOT EXISTS `share_comment` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
    `share_id` BIGINT NOT NULL COMMENT '分享ID',
    `user_id` BIGINT NOT NULL COMMENT '评论者ID',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父评论ID',
    `reply_user_id` BIGINT DEFAULT 0 COMMENT '回复用户ID',
    `content` VARCHAR(500) NOT NULL COMMENT '评论内容',
    `like_count` INT DEFAULT 0 COMMENT '点赞数',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0删除 1正常',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted` INT DEFAULT 0 COMMENT '逻辑删除',
    INDEX `idx_share_id` (`share_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- 好友关系表
CREATE TABLE IF NOT EXISTS `friendship` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关系ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `friend_id` BIGINT NOT NULL COMMENT '好友ID',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0已删除 1已关注 2已互关',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_user_friend` (`user_id`, `friend_id`),
    INDEX `idx_friend_id` (`friend_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='好友关系表';

-- 通知表
CREATE TABLE IF NOT EXISTS `notification` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '通知ID',
    `user_id` BIGINT NOT NULL COMMENT '接收者ID',
    `type` VARCHAR(20) NOT NULL COMMENT '通知类型: LIKE, COMMENT, FORWARD, FOLLOW, MENTION',
    `sender_id` BIGINT COMMENT '发送者ID',
    `related_id` BIGINT COMMENT '关联内容ID',
    `content` VARCHAR(500) COMMENT '通知内容',
    `is_read` TINYINT DEFAULT 0 COMMENT '是否已读: 0未读 1已读',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_user_type` (`user_id`, `type`, `is_read`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';
