-- 数据库表结构更新
-- 在原有基础上扩展订单表和添加支付表

USE online_mall;

-- 删除旧的订单表（如果需要重新创建）
-- DROP TABLE IF EXISTS t_order;

-- 更新订单表结构
ALTER TABLE t_order
    ADD COLUMN IF NOT EXISTS pay_amount DECIMAL(10, 2) DEFAULT 0.00 COMMENT '实付金额',
    ADD COLUMN IF NOT EXISTS freight_amount DECIMAL(10, 2) DEFAULT 0.00 COMMENT '运费金额',
    ADD COLUMN IF NOT EXISTS discount_amount DECIMAL(10, 2) DEFAULT 0.00 COMMENT '优惠金额',
    ADD COLUMN IF NOT EXISTS pay_type INT DEFAULT NULL COMMENT '支付方式 1-支付宝 2-微信',
    ADD COLUMN IF NOT EXISTS receiver_name VARCHAR(50) COMMENT '收货人姓名',
    ADD COLUMN IF NOT EXISTS receiver_phone VARCHAR(20) COMMENT '收货人电话',
    ADD COLUMN IF NOT EXISTS pay_time TIMESTAMP NULL COMMENT '支付时间',
    ADD COLUMN IF NOT EXISTS delivery_time TIMESTAMP NULL COMMENT '发货时间',
    ADD COLUMN IF NOT EXISTS receive_time TIMESTAMP NULL COMMENT '收货时间',
    ADD COLUMN IF NOT EXISTS cancel_time TIMESTAMP NULL COMMENT '取消时间',
    ADD COLUMN IF NOT EXISTS cancel_reason VARCHAR(200) COMMENT '取消原因';

-- 如果上面的 ALTER 不支持，可以重新创建表
-- 先备份再执行：

-- 重新创建订单表（完整版）
CREATE TABLE IF NOT EXISTS t_order_new (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '订单编号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    total_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '订单总金额',
    pay_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '实付金额',
    freight_amount DECIMAL(10, 2) DEFAULT 0.00 COMMENT '运费金额',
    discount_amount DECIMAL(10, 2) DEFAULT 0.00 COMMENT '优惠金额',
    pay_type INT DEFAULT NULL COMMENT '支付方式 1-支付宝 2-微信',
    status INT DEFAULT 0 COMMENT '订单状态 0-待付款 1-待发货 2-已发货 3-已完成 4-已取消 5-已退款',
    receiver_name VARCHAR(50) COMMENT '收货人姓名',
    receiver_phone VARCHAR(20) COMMENT '收货人电话',
    receiver_address VARCHAR(500) COMMENT '收货地址',
    shipping_company VARCHAR(100) COMMENT '物流公司',
    shipping_number VARCHAR(100) COMMENT '物流单号',
    remark VARCHAR(500) COMMENT '订单备注',
    pay_time TIMESTAMP NULL COMMENT '支付时间',
    delivery_time TIMESTAMP NULL COMMENT '发货时间',
    receive_time TIMESTAMP NULL COMMENT '收货时间',
    cancel_time TIMESTAMP NULL COMMENT '取消时间',
    cancel_reason VARCHAR(200) COMMENT '取消原因',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '删除标志',
    INDEX idx_user (user_id),
    INDEX idx_order_no (order_no),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- 支付记录表
CREATE TABLE IF NOT EXISTS payment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_no VARCHAR(64) NOT NULL UNIQUE COMMENT '支付流水号',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    order_no VARCHAR(50) NOT NULL COMMENT '订单编号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    amount DECIMAL(10, 2) NOT NULL COMMENT '支付金额',
    pay_type INT NOT NULL COMMENT '支付方式 1-支付宝 2-微信',
    status INT DEFAULT 0 COMMENT '支付状态 0-待支付 1-支付成功 2-支付失败 3-已退款',
    trade_no VARCHAR(100) COMMENT '第三方交易号',
    pay_time TIMESTAMP NULL COMMENT '支付时间',
    refund_time TIMESTAMP NULL COMMENT '退款时间',
    refund_amount DECIMAL(10, 2) COMMENT '退款金额',
    notify_data TEXT COMMENT '支付通知数据',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_order_id (order_id),
    INDEX idx_order_no (order_no),
    INDEX idx_user_id (user_id),
    INDEX idx_payment_no (payment_no),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支付记录表';

-- 用户地址表
CREATE TABLE IF NOT EXISTS user_address (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    receiver_name VARCHAR(50) NOT NULL COMMENT '收货人姓名',
    receiver_phone VARCHAR(20) NOT NULL COMMENT '收货人电话',
    province VARCHAR(50) COMMENT '省',
    city VARCHAR(50) COMMENT '市',
    district VARCHAR(50) COMMENT '区',
    detail_address VARCHAR(200) NOT NULL COMMENT '详细地址',
    is_default INT DEFAULT 0 COMMENT '是否默认地址 0-否 1-是',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '删除标志',
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户地址表';

-- 商品分类表
CREATE TABLE IF NOT EXISTS category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID',
    level INT DEFAULT 1 COMMENT '层级',
    sort INT DEFAULT 0 COMMENT '排序',
    icon VARCHAR(200) COMMENT '图标',
    status INT DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '删除标志',
    INDEX idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品分类表';

-- 插入测试分类数据
INSERT INTO category (name, parent_id, level, sort, status) VALUES
('数码产品', 0, 1, 1, 1),
('手机通讯', 1, 2, 1, 1),
('电脑办公', 1, 2, 2, 1),
('家用电器', 0, 1, 2, 1),
('服饰鞋包', 0, 1, 3, 1)
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- 插入测试商品数据
INSERT INTO product (name, category_id, price, original_price, stock, sales, images, description, status) VALUES
('iPhone 15 Pro Max 256GB', 2, 9999.00, 10999.00, 100, 520, '["https://via.placeholder.com/400x400?text=iPhone15"]', '苹果最新旗舰手机，A17 Pro芯片，钛金属边框', 1),
('小米14 Ultra 512GB', 2, 6499.00, 6999.00, 50, 380, '["https://via.placeholder.com/400x400?text=Mi14Ultra"]', '小米影像旗舰，徕卡光学镜头', 1),
('MacBook Pro 14英寸 M3 Pro', 3, 16999.00, 17999.00, 30, 120, '["https://via.placeholder.com/400x400?text=MacBookPro"]', 'Apple M3 Pro芯片，18GB内存', 1),
('戴尔 XPS 15 2024款', 3, 12999.00, 13999.00, 25, 85, '["https://via.placeholder.com/400x400?text=DellXPS"]', 'Intel酷睿Ultra 9处理器，RTX4060显卡', 1),
('戴森 V15吸尘器', 4, 5990.00, 6490.00, 40, 200, '["https://via.placeholder.com/400x400?text=DysonV15"]', '激光探测灰尘，智能吸力调节', 1)
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- 插入测试地址数据
INSERT INTO user_address (user_id, receiver_name, receiver_phone, province, city, district, detail_address, is_default) VALUES
(1, '张三', '13800138000', '北京市', '朝阳区', '望京街道', '望京SOHO T1 1001室', 1),
(1, '李四', '13900139000', '上海市', '浦东新区', '陆家嘴街道', '陆家嘴金融中心 A座 2001室', 0),
(2, '王五', '13700137000', '广东省', '深圳市', '南山区', '科技园腾讯大厦 3001室', 1)
ON DUPLICATE KEY UPDATE receiver_name = VALUES(receiver_name);
