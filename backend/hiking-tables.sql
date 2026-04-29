-- 徒步路线模块数据库表结构
-- 创建时间: 2026-04-29

-- =============================
-- 1. 徒步路线表
-- =============================
CREATE TABLE IF NOT EXISTS `t_hiking_route` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '路线ID',
    `name` VARCHAR(100) NOT NULL COMMENT '路线名称',
    `description` TEXT COMMENT '路线简介',
    `difficulty` VARCHAR(20) NOT NULL DEFAULT 'easy' COMMENT '难度等级: easy-简单, medium-中等, hard-困难',
    `location` VARCHAR(100) COMMENT '所在地区',
    `distance` DECIMAL(10, 2) COMMENT '距离(公里)',
    `duration` DECIMAL(5, 1) COMMENT '预计用时(小时)',
    `elevation_gain` INT COMMENT '累计爬升(米)',
    `max_elevation` INT COMMENT '最高海拔(米)',
    `best_season` VARCHAR(50) COMMENT '最佳季节',
    `tags` VARCHAR(255) COMMENT '路线标签,逗号分隔',
    `is_hot` TINYINT(1) DEFAULT 0 COMMENT '是否热门 0-否 1-是',
    `cover_image` VARCHAR(255) COMMENT '封面图片URL',
    `images` TEXT COMMENT '详情图片URL列表,JSON格式',
    `track_data` TEXT COMMENT '轨迹数据,JSON格式[[lng,lat],...]',
    `itinerary` TEXT COMMENT '行程安排,JSON格式',
    `equipment` TEXT COMMENT '装备建议,JSON格式',
    `warnings` TEXT COMMENT '注意事项,JSON格式',
    `rating` DECIMAL(2, 1) DEFAULT 5.0 COMMENT '评分 0-5',
    `review_count` INT DEFAULT 0 COMMENT '评价数量',
    `status` VARCHAR(20) DEFAULT 'draft' COMMENT '状态: draft-草稿, published-已上线, offline-已下线',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '删除标志 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    INDEX `idx_difficulty` (`difficulty`),
    INDEX `idx_location` (`location`),
    INDEX `idx_status` (`status`),
    INDEX `idx_is_hot` (`is_hot`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='徒步路线表';

-- =============================
-- 2. 路线收藏表
-- =============================
CREATE TABLE IF NOT EXISTS `t_hiking_favorite` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `route_id` BIGINT NOT NULL COMMENT '路线ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '删除标志 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_route` (`user_id`, `route_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_route_id` (`route_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='路线收藏表';

-- =============================
-- 3. 路线评价表
-- =============================
CREATE TABLE IF NOT EXISTS `t_hiking_review` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评价ID',
    `route_id` BIGINT NOT NULL COMMENT '路线ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `username` VARCHAR(50) COMMENT '用户昵称(冗余)',
    `avatar` VARCHAR(255) COMMENT '用户头像(冗余)',
    `rating` INT NOT NULL COMMENT '评分 1-5',
    `content` TEXT COMMENT '评价内容',
    `images` TEXT COMMENT '评价图片,JSON格式',
    `reply` TEXT COMMENT '商家回复',
    `reply_time` DATETIME COMMENT '回复时间',
    `status` VARCHAR(20) DEFAULT 'pending' COMMENT '状态: pending-待审核, approved-已通过, rejected-已拒绝',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '删除标志 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    INDEX `idx_route_id` (`route_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='路线评价表';

-- =============================
-- 4. 插入测试数据
-- =============================
INSERT INTO `t_hiking_route` (`name`, `description`, `difficulty`, `location`, `distance`, `duration`,
    `elevation_gain`, `max_elevation`, `best_season`, `tags`, `is_hot`, `cover_image`, `status`, `rating`, `review_count`)
VALUES
('香山红叶徒步路线', '香山红叶徒步路线是北京最经典的秋季徒步路线之一。路线从香山公园东门出发，途经双清别墅、香炉峰，最后到达鬼见愁。全程约8.5公里，爬升450米，适合各个年龄段的徒步爱好者。每年10月中旬至11月初是最佳观赏期，满山的红叶美不胜收。',
    'easy', '北京', 8.5, 3.0, 450, 575, '9月-11月', '风景优美,红叶观赏,适合新手,亲子友好', 1,
    'https://picsum.photos/800/600?random=1', 'published', 4.8, 256),

('灵山大峡谷穿越', '灵山大峡谷穿越路线是北京周边最具挑战性的徒步路线之一。全程约18公里，需要8小时完成，累计爬升1200米。沿途风景壮丽，有峡谷、溪流、瀑布等多种地貌，适合有经验的徒步爱好者。',
    'hard', '北京', 18.2, 8.0, 1200, 1500, '5月-10月', '挑战性强,风景优美,峡谷穿越', 1,
    'https://picsum.photos/800/600?random=2', 'published', 4.9, 128),

('西湖环湖步道', '西湖环湖步道是杭州最经典的徒步路线。全程约12公里，路面平坦，适合各个年龄段。沿途可以欣赏西湖十景，体验江南水乡的韵味。',
    'easy', '杭州', 12.0, 4.0, 80, 100, '全年', '适合新手,亲子友好,西湖十景', 0,
    'https://picsum.photos/800/600?random=3', 'published', 4.7, 512),

('四姑娘山大峰攀登', '四姑娘山大峰攀登是川西高原最经典的入门级雪山攀登路线。全程约28公里，需要2天时间，海拔从3200米上升到5025米。沿途可以欣赏到雪山、草甸、海子等高原风光。',
    'hard', '四川', 28.5, 12.0, 2200, 5025, '6月-10月', '挑战性强,摄影圣地,雪山攀登', 1,
    'https://picsum.photos/800/600?random=4', 'published', 4.9, 89),

('莫干山竹林小径', '莫干山竹林小径穿越在万亩竹林之中，空气清新，环境清幽。全程约10公里，路况良好，适合周末休闲徒步。夏季是避暑胜地，温度比市区低5-8度。',
    'medium', '浙江', 10.5, 4.5, 380, 720, '4月-11月', '避暑胜地,亲子友好,竹林风光', 0,
    'https://picsum.photos/800/600?random=5', 'published', 4.6, 334),

('泰山经典登山路线', '泰山五岳之首，自古以来就是帝王封禅之地。经典登山路线从红门出发，经过中天门、十八盘，最终到达玉皇顶。全程约9.5公里，爬升1400米，一般需要6小时。',
    'medium', '山东', 9.5, 6.0, 1400, 1545, '4月-10月', '历史文化,挑战性强,五岳之首', 1,
    'https://picsum.photos/800/600?random=6', 'published', 4.8, 892),

('喀纳斯湖环湖徒步', '喀纳斯湖环湖徒步路线可以近距离欣赏"东方瑞士"的美景。全程约25公里，沿着湖畔前行，可以欣赏到湖光山色、原始森林和草原风光。',
    'medium', '新疆', 25.0, 10.0, 600, 1374, '6月-9月', '风景优美,摄影圣地,湖光山色', 1,
    'https://picsum.photos/800/600?random=7', 'published', 4.9, 156),

('黄山云谷寺步道', '黄山云谷寺步道是黄山最经典的登山路线之一。从云谷寺出发，经过白鹅岭、光明顶，最后到达迎客松。全程约15公里，可以欣赏到黄山的奇松、怪石、云海。',
    'medium', '安徽', 15.0, 7.0, 1000, 1864, '4月-11月', '风景优美,摄影圣地,奇松怪石', 1,
    'https://picsum.photos/800/600?random=8', 'published', 4.8, 445);
