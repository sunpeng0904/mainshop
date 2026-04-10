-- 数据库内容国际化表结构
-- 执行时间: 2026-04-09

USE online_mall;

-- 商品多语言表
CREATE TABLE IF NOT EXISTS `product_i18n` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `locale` VARCHAR(10) NOT NULL COMMENT '语言代码（zh_CN, en_US）',
    `name` VARCHAR(255) DEFAULT NULL COMMENT '商品名称',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '商品描述',
    `detail` TEXT COMMENT '商品详情',
    `specifications` VARCHAR(1000) DEFAULT NULL COMMENT '规格参数',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_product_locale` (`product_id`, `locale`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_locale` (`locale`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品多语言信息表';

-- 分类多语言表
CREATE TABLE IF NOT EXISTS `category_i18n` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `category_id` BIGINT NOT NULL COMMENT '分类ID',
    `locale` VARCHAR(10) NOT NULL COMMENT '语言代码',
    `name` VARCHAR(100) NOT NULL COMMENT '分类名称',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '分类描述',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_category_locale` (`category_id`, `locale`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_locale` (`locale`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类多语言信息表';

-- 插入示例数据：商品英文翻译
INSERT INTO product_i18n (product_id, locale, name, description) VALUES
(1, 'en_US', 'iPhone 15 Pro Max 256GB', 'Apple latest flagship phone, A17 Pro chip, titanium frame'),
(2, 'en_US', 'Xiaomi 14 Ultra 512GB', 'Xiaomi flagship camera phone, Leica optical lens'),
(3, 'en_US', 'MacBook Pro 14-inch M3 Pro', 'Apple M3 Pro chip, 18GB RAM'),
(4, 'en_US', 'Dell XPS 15 2024', 'Intel Core Ultra 9, RTX4060 graphics'),
(5, 'en_US', 'Dyson V15 Vacuum', 'Laser dust detection, smart suction adjustment')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- 插入示例数据：分类英文翻译
INSERT INTO category_i18n (category_id, locale, name, description) VALUES
(1, 'en_US', 'Digital Products', 'Digital devices and electronics'),
(2, 'en_US', 'Mobile Phones', 'Smartphones and mobile devices'),
(3, 'en_US', 'Computers & Office', 'Laptops, desktops and office equipment'),
(4, 'en_US', 'Home Appliances', 'Home and kitchen appliances'),
(5, 'en_US', 'Fashion & Shoes', 'Clothing, shoes and accessories')
ON DUPLICATE KEY UPDATE name = VALUES(name);
