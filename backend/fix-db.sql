-- 在线商城数据库更新脚本
-- 请在 MySQL 中执行此脚本

USE online_mall;

-- 1. 更新订单表，添加新字段
ALTER TABLE t_order
    ADD COLUMN IF NOT EXISTS pay_amount DECIMAL(10, 2) DEFAULT 0.00 COMMENT '实付金额',
    ADD COLUMN IF NOT EXISTS freight_amount DECIMAL(10, 2) DEFAULT 0.00 COMMENT '运费金额',
    ADD COLUMN IF NOT EXISTS discount_amount DECIMAL(10, 2) DEFAULT 0.00 COMMENT '优惠金额',
    ADD COLUMN IF NOT EXISTS pay_type INT DEFAULT NULL COMMENT '支付方式 1-支付宝 2-微信',
    ADD COLUMN IF NOT EXISTS receiver_name VARCHAR(50) COMMENT '收货人姓名',
    ADD COLUMN IF NOT EXISTS receiver_phone VARCHAR(20) COMMENT '收货人电话',
    ADD COLUMN IF NOT EXISTS receiver_address VARCHAR(500) COMMENT '收货地址',
    ADD COLUMN IF NOT EXISTS pay_time DATETIME NULL COMMENT '支付时间',
    ADD COLUMN IF NOT EXISTS delivery_time DATETIME NULL COMMENT '发货时间',
    ADD COLUMN IF NOT EXISTS receive_time DATETIME NULL COMMENT '收货时间',
    ADD COLUMN IF NOT EXISTS cancel_time DATETIME NULL COMMENT '取消时间',
    ADD COLUMN IF NOT EXISTS cancel_reason VARCHAR(200) COMMENT '取消原因';

-- 如果上面的 ALTER 不支持 IF NOT EXISTS，请执行以下语句：
-- MySQL 5.7 或更低版本使用以下方式：

-- 先检查并添加字段（如果字段不存在）
SET @dbname = DATABASE();
SET @tablename = 't_order';

-- 添加 pay_amount
SET @columnname = 'pay_amount';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @columnname) > 0,
  'SELECT 1',
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' DECIMAL(10, 2) DEFAULT 0.00 COMMENT ''实付金额''')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- 添加 receiver_address
SET @columnname = 'receiver_address';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @columnname) > 0,
  'SELECT 1',
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' VARCHAR(500) COMMENT ''收货地址''')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- 添加 receiver_name
SET @columnname = 'receiver_name';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @columnname) > 0,
  'SELECT 1',
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' VARCHAR(50) COMMENT ''收货人姓名''')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- 添加 receiver_phone
SET @columnname = 'receiver_phone';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @columnname) > 0,
  'SELECT 1',
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' VARCHAR(20) COMMENT ''收货人电话''')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- 添加 pay_type
SET @columnname = 'pay_type';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @columnname) > 0,
  'SELECT 1',
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' INT DEFAULT NULL COMMENT ''支付方式''')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- 添加 freight_amount
SET @columnname = 'freight_amount';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @columnname) > 0,
  'SELECT 1',
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' DECIMAL(10, 2) DEFAULT 0.00 COMMENT ''运费金额''')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- 添加 discount_amount
SET @columnname = 'discount_amount';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @columnname) > 0,
  'SELECT 1',
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' DECIMAL(10, 2) DEFAULT 0.00 COMMENT ''优惠金额''')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- 添加 pay_time
SET @columnname = 'pay_time';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @columnname) > 0,
  'SELECT 1',
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' DATETIME NULL COMMENT ''支付时间''')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- 添加 delivery_time
SET @columnname = 'delivery_time';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @columnname) > 0,
  'SELECT 1',
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' DATETIME NULL COMMENT ''发货时间''')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- 添加 receive_time
SET @columnname = 'receive_time';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @columnname) > 0,
  'SELECT 1',
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' DATETIME NULL COMMENT ''收货时间''')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- 添加 cancel_time
SET @columnname = 'cancel_time';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @columnname) > 0,
  'SELECT 1',
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' DATETIME NULL COMMENT ''取消时间''')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- 添加 cancel_reason
SET @columnname = 'cancel_reason';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @columnname) > 0,
  'SELECT 1',
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' VARCHAR(200) COMMENT ''取消原因''')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- 2. 创建支付记录表
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
    pay_time DATETIME NULL COMMENT '支付时间',
    refund_time DATETIME NULL COMMENT '退款时间',
    refund_amount DECIMAL(10, 2) COMMENT '退款金额',
    notify_data TEXT COMMENT '支付通知数据',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_order_id (order_id),
    INDEX idx_order_no (order_no),
    INDEX idx_user_id (user_id),
    INDEX idx_payment_no (payment_no),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支付记录表';

-- 3. 创建用户地址表
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
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '删除标志',
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户地址表';

-- 4. 创建商品分类表
CREATE TABLE IF NOT EXISTS category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID',
    level INT DEFAULT 1 COMMENT '层级',
    sort INT DEFAULT 0 COMMENT '排序',
    icon VARCHAR(200) COMMENT '图标',
    status INT DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '删除标志',
    INDEX idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品分类表';

-- 5. 插入测试分类数据
INSERT IGNORE INTO category (id, name, parent_id, level, sort, status) VALUES
(1, '数码产品', 0, 1, 1, 1),
(2, '手机通讯', 1, 2, 1, 1),
(3, '电脑办公', 1, 2, 2, 1),
(4, '家用电器', 0, 1, 2, 1),
(5, '服饰鞋包', 0, 1, 3, 1);

-- 6. 插入测试商品数据
INSERT IGNORE INTO product (id, name, category_id, price, original_price, stock, sales, images, description, status) VALUES
(1, 'iPhone 15 Pro Max 256GB', 2, 9999.00, 10999.00, 100, 520, '["https://via.placeholder.com/400x400?text=iPhone15"]', '苹果最新旗舰手机，A17 Pro芯片，钛金属边框', 1),
(2, '小米14 Ultra 512GB', 2, 6499.00, 6999.00, 50, 380, '["https://via.placeholder.com/400x400?text=Mi14Ultra"]', '小米影像旗舰，徕卡光学镜头', 1),
(3, 'MacBook Pro 14英寸 M3 Pro', 3, 16999.00, 17999.00, 30, 120, '["https://via.placeholder.com/400x400?text=MacBookPro"]', 'Apple M3 Pro芯片，18GB内存', 1),
(4, '戴尔 XPS 15 2024款', 3, 12999.00, 13999.00, 25, 85, '["https://via.placeholder.com/400x400?text=DellXPS"]', 'Intel酷睿Ultra 9处理器，RTX4060显卡', 1),
(5, '戴森 V15吸尘器', 4, 5990.00, 6490.00, 40, 200, '["https://via.placeholder.com/400x400?text=DysonV15"]', '激光探测灰尘，智能吸力调节', 1);

-- 7. 插入测试地址数据
INSERT IGNORE INTO user_address (id, user_id, receiver_name, receiver_phone, province, city, district, detail_address, is_default) VALUES
(1, 1, '张三', '13800138000', '北京市', '朝阳区', '望京街道', '望京SOHO T1 1001室', 1),
(2, 1, '李四', '13900139000', '上海市', '浦东新区', '陆家嘴街道', '陆家嘴金融中心 A座 2001室', 0),
(3, 2, '王五', '13700137000', '广东省', '深圳市', '南山区', '科技园腾讯大厦 3001室', 1);

SELECT '数据库更新完成!' AS message;
