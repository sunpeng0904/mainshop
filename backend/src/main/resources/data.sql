-- 初始化管理员用户（密码：admin123）
INSERT INTO t_user (username, password, email, phone, nickname, gender, status, avatar)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'admin@example.com', '13800000000', '管理员', 1, 1, 'https://api.dicebear.com/7.x/avataaars/svg?seed=admin')
ON DUPLICATE KEY UPDATE username = username;

-- 初始化测试用户（密码：123456）
INSERT INTO t_user (username, password, email, phone, nickname, gender, status, avatar)
VALUES ('test', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'test@example.com', '13800000001', '测试用户', 1, 1, 'https://api.dicebear.com/7.x/avataaars/svg?seed=test')
ON DUPLICATE KEY UPDATE username = username;

-- 初始化角色
INSERT INTO t_role (id, role_code, role_name, description, sort, status)
VALUES (1, 'ROLE_ADMIN', '管理员', '系统管理员，拥有所有权限', 1, 1)
ON DUPLICATE KEY UPDATE role_code = role_code;

INSERT INTO t_role (id, role_code, role_name, description, sort, status)
VALUES (2, 'ROLE_USER', '普通用户', '普通用户，拥有基本权限', 2, 1)
ON DUPLICATE KEY UPDATE role_code = role_code;

-- 给admin用户分配管理员角色
INSERT INTO t_user_role (user_id, role_id)
SELECT u.id, 1 FROM t_user u WHERE u.username = 'admin'
ON DUPLICATE KEY UPDATE user_id = user_id;

-- 给所有用户分配普通用户角色
INSERT INTO t_user_role (user_id, role_id)
SELECT u.id, 2 FROM t_user u
ON DUPLICATE KEY UPDATE user_id = user_id;

-- 初始化分类
INSERT INTO category (id, name, parent_id, level, sort, status, icon)
VALUES (1, '手机数码', 0, 1, 1, 1, 'Mobile')
ON DUPLICATE KEY UPDATE name = name;

INSERT INTO category (id, name, parent_id, level, sort, status)
VALUES (2, '手机通讯', 1, 2, 1, 1)
ON DUPLICATE KEY UPDATE name = name;

INSERT INTO category (id, name, parent_id, level, sort, status)
VALUES (3, '手机配件', 1, 2, 2, 1)
ON DUPLICATE KEY UPDATE name = name;

INSERT INTO category (id, name, parent_id, level, sort, status, icon)
VALUES (4, '电脑办公', 0, 1, 2, 1, 'Monitor')
ON DUPLICATE KEY UPDATE name = name;

INSERT INTO category (id, name, parent_id, level, sort, status)
VALUES (5, '电脑整机', 4, 2, 1, 1)
ON DUPLICATE KEY UPDATE name = name;

INSERT INTO category (id, name, parent_id, level, sort, status)
VALUES (6, '电脑配件', 4, 2, 2, 1)
ON DUPLICATE KEY UPDATE name = name;

INSERT INTO category (id, name, parent_id, level, sort, status, icon)
VALUES (7, '家用电器', 0, 1, 3, 1, 'Coffee')
ON DUPLICATE KEY UPDATE name = name;

INSERT INTO category (id, name, parent_id, level, sort, status)
VALUES (8, '大家电', 7, 2, 1, 1)
ON DUPLICATE KEY UPDATE name = name;

INSERT INTO category (id, name, parent_id, level, sort, status)
VALUES (9, '生活电器', 7, 2, 2, 1)
ON DUPLICATE KEY UPDATE name = name;

INSERT INTO category (id, name, parent_id, level, sort, status, icon)
VALUES (10, '服饰鞋包', 0, 1, 4, 1, 'ShoppingBag')
ON DUPLICATE KEY UPDATE name = name;

-- 初始化商品数据（图片使用本地静态资源路径）
INSERT INTO product (id, name, category_id, price, original_price, stock, sales, images, description, status)
VALUES (1, 'iPhone 15 Pro Max 256GB 原色钛金属', 2, 9999.00, 10999.00, 100, 128, '["/images/products/product_1_1.jpg", "/images/products/product_1_2.jpg"]', 'Apple iPhone 15 Pro Max，A17 Pro芯片，钛金属设计', 1)
ON DUPLICATE KEY UPDATE name = name;

INSERT INTO product (id, name, category_id, price, original_price, stock, sales, images, description, status)
VALUES (2, '华为 Mate 60 Pro 12GB+512GB', 2, 7999.00, 8999.00, 80, 256, '["/images/products/product_2_1.jpg", "/images/products/product_2_2.jpg"]', '华为 Mate 60 Pro，麒麟9000S处理器，卫星通信', 1)
ON DUPLICATE KEY UPDATE name = name;

INSERT INTO product (id, name, category_id, price, original_price, stock, sales, images, description, status)
VALUES (3, '小米14 Ultra 16GB+512GB', 2, 6499.00, 6999.00, 120, 89, '["/images/products/product_3_1.jpg", "/images/products/product_3_2.jpg"]', '小米14 Ultra，徕卡光学镜头，骁龙8 Gen3', 1)
ON DUPLICATE KEY UPDATE name = name;

INSERT INTO product (id, name, category_id, price, original_price, stock, sales, images, description, status)
VALUES (4, 'MacBook Pro 14英寸 M3 Pro', 5, 16999.00, 18999.00, 50, 67, '["/images/products/product_4_1.jpg", "/images/products/product_4_2.jpg"]', 'MacBook Pro 14英寸，M3 Pro芯片，18小时续航', 1)
ON DUPLICATE KEY UPDATE name = name;

INSERT INTO product (id, name, category_id, price, original_price, stock, sales, images, description, status)
VALUES (5, 'ThinkPad X1 Carbon 2024', 5, 12999.00, 14999.00, 60, 45, '["/images/products/product_5_1.jpg", "/images/products/product_5_2.jpg"]', 'ThinkPad X1 Carbon，Intel酷睿Ultra7，轻薄商务本', 1)
ON DUPLICATE KEY UPDATE name = name;

INSERT INTO product (id, name, category_id, price, original_price, stock, sales, images, description, status)
VALUES (6, 'AirPods Pro 2代 USB-C', 3, 1799.00, 1999.00, 200, 520, '["/images/products/product_6_1.jpg", "/images/products/product_6_2.jpg"]', 'Apple AirPods Pro 2代，主动降噪，自适应音频', 1)
ON DUPLICATE KEY UPDATE name = name;

INSERT INTO product (id, name, category_id, price, original_price, stock, sales, images, description, status)
VALUES (7, 'Sony WH-1000XM5 头戴式耳机', 3, 2499.00, 2999.00, 80, 156, '["/images/products/product_7_1.jpg", "/images/products/product_7_2.jpg"]', 'Sony WH-1000XM5，业界领先降噪，30小时续航', 1)
ON DUPLICATE KEY UPDATE name = name;

INSERT INTO product (id, name, category_id, price, original_price, stock, sales, images, description, status)
VALUES (8, '戴森 V15 吸尘器', 9, 4990.00, 5990.00, 40, 78, '["/images/products/product_8_1.jpg", "/images/products/product_8_2.jpg"]', '戴森 V15 Detect，激光探测灰尘，智能感应', 1)
ON DUPLICATE KEY UPDATE name = name;

INSERT INTO product (id, name, category_id, price, original_price, stock, sales, images, description, status)
VALUES (9, '美的空调 3匹 新一级能效', 8, 5999.00, 7999.00, 30, 34, '["/images/products/product_9_1.jpg", "/images/products/product_9_2.jpg"]', '美的空调，新一级能效，智能温控，静音设计', 1)
ON DUPLICATE KEY UPDATE name = name;

INSERT INTO product (id, name, category_id, price, original_price, stock, sales, images, description, status)
VALUES (10, '西门子冰箱 500L 对开门', 8, 7999.00, 9999.00, 25, 23, '["/images/products/product_10_1.jpg", "/images/products/product_10_2.jpg"]', '西门子对开门冰箱，500L大容量，变频节能', 1)
ON DUPLICATE KEY UPDATE name = name;

INSERT INTO product (id, name, category_id, price, original_price, stock, sales, images, description, status)
VALUES (11, 'RTX 4090 显卡 24GB', 6, 14999.00, 16999.00, 20, 45, '["/images/products/product_11_1.jpg", "/images/products/product_11_2.jpg"]', 'NVIDIA RTX 4090，24GB显存，旗舰级游戏显卡', 1)
ON DUPLICATE KEY UPDATE name = name;

INSERT INTO product (id, name, category_id, price, original_price, stock, sales, images, description, status)
VALUES (12, 'Samsung 990 Pro 2TB SSD', 6, 1299.00, 1599.00, 150, 89, '["/images/products/product_12_1.jpg", "/images/products/product_12_2.jpg"]', '三星990 Pro，PCIe 4.0，读写速度7450MB/s', 1)
ON DUPLICATE KEY UPDATE name = name;

-- 初始化省份数据（先清除旧数据）
DELETE FROM region_tb WHERE lvl = 1;
INSERT INTO region_tb (id, cde, name, prnt_cde, lvl, vld_sts_cde) VALUES
('R001', '110000', '北京市', '0', 1, '0'),
('R002', '120000', '天津市', '0', 1, '0'),
('R003', '130000', '河北省', '0', 1, '0'),
('R004', '140000', '山西省', '0', 1, '0'),
('R005', '150000', '内蒙古自治区', '0', 1, '0'),
('R006', '210000', '辽宁省', '0', 1, '0'),
('R007', '220000', '吉林省', '0', 1, '0'),
('R008', '230000', '黑龙江省', '0', 1, '0'),
('R009', '310000', '上海市', '0', 1, '0'),
('R010', '320000', '江苏省', '0', 1, '0'),
('R011', '330000', '浙江省', '0', 1, '0'),
('R012', '340000', '安徽省', '0', 1, '0'),
('R013', '350000', '福建省', '0', 1, '0'),
('R014', '360000', '江西省', '0', 1, '0'),
('R015', '370000', '山东省', '0', 1, '0'),
('R016', '410000', '河南省', '0', 1, '0'),
('R017', '420000', '湖北省', '0', 1, '0'),
('R018', '430000', '湖南省', '0', 1, '0'),
('R019', '440000', '广东省', '0', 1, '0'),
('R020', '450000', '广西壮族自治区', '0', 1, '0'),
('R021', '460000', '海南省', '0', 1, '0'),
('R022', '500000', '重庆市', '0', 1, '0'),
('R023', '510000', '四川省', '0', 1, '0'),
('R024', '520000', '贵州省', '0', 1, '0'),
('R025', '530000', '云南省', '0', 1, '0'),
('R026', '540000', '西藏自治区', '0', 1, '0'),
('R027', '610000', '陕西省', '0', 1, '0'),
('R028', '620000', '甘肃省', '0', 1, '0'),
('R029', '630000', '青海省', '0', 1, '0'),
('R030', '640000', '宁夏回族自治区', '0', 1, '0'),
('R031', '650000', '新疆维吾尔自治区', '0', 1, '0')
ON DUPLICATE KEY UPDATE name = name;
