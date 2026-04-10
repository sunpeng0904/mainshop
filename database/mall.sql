-- 在线商城数据库初始化脚本
-- 数据库: online_mall
-- 用户: root/root
-- 主机: 127.0.0.1:3306

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `online_mall` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `online_mall`;

-- 用户表
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `gender` tinyint DEFAULT '0' COMMENT '性别 0-未知 1-男 2-女',
  `birthday` date DEFAULT NULL COMMENT '生日',
  `status` tinyint DEFAULT '1' COMMENT '状态 0-禁用 1-正常',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '删除标志 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`),
  KEY `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 商品分类表
CREATE TABLE `category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `parent_id` bigint DEFAULT '0' COMMENT '父分类ID',
  `level` tinyint DEFAULT '1' COMMENT '分类层级',
  `sort` int DEFAULT '0' COMMENT '排序',
  `icon` varchar(255) DEFAULT NULL COMMENT '图标',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `status` tinyint DEFAULT '1' COMMENT '状态 0-禁用 1-启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品分类表';

-- 商品表
CREATE TABLE `product` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `name` varchar(100) NOT NULL COMMENT '商品名称',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `price` decimal(10,2) NOT NULL COMMENT '价格',
  `original_price` decimal(10,2) DEFAULT NULL COMMENT '原价',
  `stock` int NOT NULL DEFAULT '0' COMMENT '库存',
  `sales` int DEFAULT '0' COMMENT '销量',
  `images` text COMMENT '商品图片JSON数组',
  `description` text COMMENT '商品描述',
  `detail` longtext COMMENT '商品详情',
  `specifications` json DEFAULT NULL COMMENT '规格参数',
  `status` tinyint DEFAULT '1' COMMENT '状态 0-下架 1-上架',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';

-- 购物车表
CREATE TABLE `cart` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `quantity` int NOT NULL DEFAULT '1' COMMENT '数量',
  `selected` tinyint DEFAULT '1' COMMENT '是否选中 0-否 1-是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_product` (`user_id`,`product_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='购物车表';

-- 订单表
CREATE TABLE `order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单编号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `total_amount` decimal(10,2) NOT NULL COMMENT '订单总金额',
  `pay_amount` decimal(10,2) NOT NULL COMMENT '实付金额',
  `freight_amount` decimal(10,2) DEFAULT '0.00' COMMENT '运费金额',
  `discount_amount` decimal(10,2) DEFAULT '0.00' COMMENT '优惠金额',
  `pay_type` tinyint DEFAULT NULL COMMENT '支付方式 1-支付宝 2-微信',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '订单状态 0-待付款 1-待发货 2-已发货 3-已完成 4-已取消',
  `receiver_name` varchar(50) NOT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(20) NOT NULL COMMENT '收货人电话',
  `receiver_address` varchar(255) NOT NULL COMMENT '收货地址',
  `remark` varchar(255) DEFAULT NULL COMMENT '订单备注',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `delivery_time` datetime DEFAULT NULL COMMENT '发货时间',
  `receive_time` datetime DEFAULT NULL COMMENT '收货时间',
  `cancel_time` datetime DEFAULT NULL COMMENT '取消时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- 订单商品表
CREATE TABLE `order_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单商品ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `product_name` varchar(100) NOT NULL COMMENT '商品名称',
  `product_image` varchar(255) DEFAULT NULL COMMENT '商品图片',
  `price` decimal(10,2) NOT NULL COMMENT '商品单价',
  `quantity` int NOT NULL COMMENT '购买数量',
  `total_amount` decimal(10,2) NOT NULL COMMENT '商品总价',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单商品表';

-- 收货地址表
CREATE TABLE `address` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `receiver_name` varchar(50) NOT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(20) NOT NULL COMMENT '收货人电话',
  `province` varchar(50) NOT NULL COMMENT '省',
  `city` varchar(50) NOT NULL COMMENT '市',
  `district` varchar(50) NOT NULL COMMENT '区',
  `detail_address` varchar(255) NOT NULL COMMENT '详细地址',
  `is_default` tinyint DEFAULT '0' COMMENT '是否默认 0-否 1-是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收货地址表';

-- 插入测试数据

-- 插入用户
INSERT INTO `user` (`username`, `password`, `email`, `phone`, `nickname`, `gender`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVwE.', 'admin@example.com', '13800138000', '管理员', 1),
('user1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVwE.', 'user1@example.com', '13800138001', '用户1', 0),
('user2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVwE.', 'user2@example.com', '13800138002', '用户2', 2);

-- 插入商品分类
INSERT INTO `category` (`name`, `parent_id`, `level`, `sort`, `description`) VALUES
('电子产品', 0, 1, 1, '手机、电脑、平板等'),
('服装鞋帽', 0, 1, 2, '男女服装、鞋子、配饰'),
('家居生活', 0, 1, 3, '家具、家电、日用品'),
('手机', 1, 2, 1, '智能手机'),
('笔记本电脑', 1, 2, 2, '便携式电脑'),
('男装', 2, 2, 1, '男士服装'),
('女装', 2, 2, 2, '女士服装');

-- 插入商品
INSERT INTO `product` (`name`, `category_id`, `price`, `original_price`, `stock`, `images`, `description`) VALUES
('iPhone 15 Pro', 4, 8999.00, 9999.00, 100, '["iphone15-1.jpg", "iphone15-2.jpg"]', '苹果最新旗舰手机'),
('华为 Mate 60', 4, 6999.00, 7999.00, 150, '["mate60-1.jpg", "mate60-2.jpg"]', '华为旗舰手机'),
('MacBook Pro 16寸', 5, 18999.00, 19999.00, 50, '["macbook-1.jpg", "macbook-2.jpg"]', '苹果专业笔记本电脑'),
('联想 ThinkPad X1', 5, 12999.00, 13999.00, 80, '["thinkpad-1.jpg", "thinkpad-2.jpg"]', '商务笔记本电脑'),
('男士休闲衬衫', 6, 299.00, 399.00, 200, '["shirt-1.jpg", "shirt-2.jpg"]', '纯棉男士衬衫'),
('女士连衣裙', 7, 499.00, 599.00, 150, '["dress-1.jpg", "dress-2.jpg"]', '夏季新款连衣裙');

-- 插入收货地址
INSERT INTO `address` (`user_id`, `receiver_name`, `receiver_phone`, `province`, `city`, `district`, `detail_address`, `is_default`) VALUES
(1, '张三', '13800138000', '北京市', '北京市', '朝阳区', '建国路88号', 1),
(2, '李四', '13800138001', '上海市', '上海市', '浦东新区', '张江高科技园区', 1);

-- 创建视图
CREATE VIEW `product_view` AS
SELECT 
    p.*,
    c.name as category_name,
    c.parent_id as category_parent_id
FROM `product` p
LEFT JOIN `category` c ON p.category_id = c.id;

-- 创建存储过程：获取用户订单统计
DELIMITER //
CREATE PROCEDURE `get_user_order_stats`(IN user_id BIGINT)
BEGIN
    SELECT 
        COUNT(*) as total_orders,
        SUM(total_amount) as total_amount,
        AVG(total_amount) as avg_amount,
        MAX(create_time) as last_order_time
    FROM `order`
    WHERE user_id = user_id AND status != 4;
END //
DELIMITER ;

-- 创建函数：生成订单号
DELIMITER //
CREATE FUNCTION `generate_order_no`() RETURNS VARCHAR(32)
BEGIN
    DECLARE order_no VARCHAR(32);
    SET order_no = CONCAT(
        DATE_FORMAT(NOW(), '%Y%m%d%H%i%s'),
        LPAD(FLOOR(RAND() * 10000), 4, '0')
    );
    RETURN order_no;
END //
DELIMITER ;

-- 创建事件：清理过期购物车（每天凌晨执行）
DELIMITER //
CREATE EVENT `clean_expired_carts`
ON SCHEDULE EVERY 1 DAY
STARTS '2025-04-08 03:00:00'
DO
BEGIN
    DELETE FROM `cart` 
    WHERE create_time < DATE_SUB(NOW(), INTERVAL 30 DAY);
END //
DELIMITER ;

-- 创建触发器：更新商品销量
DELIMITER //
CREATE TRIGGER `update_product_sales`
AFTER INSERT ON `order_item`
FOR EACH ROW
BEGIN
    UPDATE `product` 
    SET sales = sales + NEW.quantity,
        stock = stock - NEW.quantity
    WHERE id = NEW.product_id;
END //
DELIMITER ;

-- 查看表结构
SHOW TABLES;

-- 查看表记录数
SELECT 
    TABLE_NAME,
    TABLE_ROWS
FROM INFORMATION_SCHEMA.TABLES
WHERE TABLE_SCHEMA = 'online_mall'
ORDER BY TABLE_NAME;