-- 更新商品表，添加缺失字段
USE online_mall;

-- 添加 description 字段
SET @dbname = DATABASE();
SET @tablename = 'product';

SET @columnname = 'description';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @columnname) > 0,
  'SELECT 1',
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' TEXT COMMENT ''商品描述''')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

SET @columnname = 'detail';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @columnname) > 0,
  'SELECT 1',
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' TEXT COMMENT ''商品详情''')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

SET @columnname = 'specifications';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @columnname) > 0,
  'SELECT 1',
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' TEXT COMMENT ''规格参数''')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

SET @columnname = 'original_price';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @columnname) > 0,
  'SELECT 1',
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' DECIMAL(10, 2) COMMENT ''原价''')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

SET @columnname = 'category_id';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @columnname) > 0,
  'SELECT 1',
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' BIGINT COMMENT ''分类ID''')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

SET @columnname = 'sales';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @columnname) > 0,
  'SELECT 1',
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' INT DEFAULT 0 COMMENT ''销量''')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

SET @columnname = 'status';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @columnname) > 0,
  'SELECT 1',
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' INT DEFAULT 1 COMMENT ''状态 0-下架 1-上架''')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

SET @columnname = 'create_time';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @columnname) > 0,
  'SELECT 1',
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间''')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

SET @columnname = 'update_time';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @columnname) > 0,
  'SELECT 1',
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间''')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

SET @columnname = 'deleted';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @columnname) > 0,
  'SELECT 1',
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' INT DEFAULT 0 COMMENT ''删除标志''')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- 添加索引
ALTER TABLE product ADD INDEX IF NOT EXISTS idx_category (category_id);
ALTER TABLE product ADD INDEX IF NOT EXISTS idx_status (status);

-- 清空旧数据并插入新的测试商品数据
DELETE FROM product;

INSERT INTO product (id, name, category_id, price, original_price, stock, sales, images, description, detail, specifications, status) VALUES
(1, 'iPhone 15 Pro Max 256GB', 2, 9999.00, 10999.00, 100, 520, '["https://picsum.photos/400/400?random=1"]', '苹果最新旗舰手机，A17 Pro芯片，钛金属边框，120Hz ProMotion显示屏', '<p>iPhone 15 Pro Max 是苹果公司于2023年发布的旗舰手机产品</p><ul><li>A17 Pro芯片</li><li>6.7英寸超视网膜XDR显示屏</li><li>4800万像素主摄</li></ul>', '{"存储":"256GB","颜色":"深空黑","屏幕":"6.7英寸"}', 1),
(2, '小米14 Ultra 512GB', 2, 6499.00, 6999.00, 50, 380, '["https://picsum.photos/400/400?random=2"]', '小米影像旗舰，徕卡光学镜头，骁龙8 Gen3处理器', '<p>小米14 Ultra 是小米公司最新推出的影像旗舰手机</p><ul><li>骁龙8 Gen3处理器</li><li>徕卡专业光学镜头</li><li>5000mAh大电池</li></ul>', '{"存储":"512GB","颜色":"黑色","屏幕":"6.73英寸"}', 1),
(3, 'MacBook Pro 14英寸 M3 Pro', 3, 16999.00, 17999.00, 30, 120, '["https://picsum.photos/400/400?random=3"]', 'Apple M3 Pro芯片，18GB内存，超强性能笔记本', '<p>MacBook Pro 14英寸搭载M3 Pro芯片</p><ul><li>M3 Pro芯片</li><li>18GB统一内存</li><li>18小时续航</li></ul>', '{"芯片":"M3 Pro","内存":"18GB","存储":"512GB SSD"}', 1),
(4, '戴尔 XPS 15 2024款', 3, 12999.00, 13999.00, 25, 85, '["https://picsum.photos/400/400?random=4"]', 'Intel酷睿Ultra 9处理器，RTX4060显卡，创作者首选', '<p>戴尔XPS 15 2024款是面向创作者的高性能笔记本</p><ul><li>Intel酷睿Ultra 9</li><li>RTX 4060显卡</li><li>3.5K OLED屏幕</li></ul>', '{"处理器":"Ultra 9 185H","显卡":"RTX 4060","屏幕":"15.6英寸 3.5K OLED"}', 1),
(5, '戴森 V15吸尘器', 4, 5990.00, 6490.00, 40, 200, '["https://picsum.photos/400/400?random=5"]', '激光探测灰尘，智能吸力调节，清洁神器', '<p>戴森V15 Detect是戴森最新的旗舰吸尘器</p><ul><li>激光探测技术</li><li>智能吸力调节</li><li>60分钟续航</li></ul>', '{"功率":"240AW","续航":"60分钟","重量":"2.6kg"}', 1),
(6, '华为 Mate 60 Pro', 2, 6999.00, 7499.00, 80, 650, '["https://picsum.photos/400/400?random=6"]', '麒麟芯片回归，卫星通话，国产旗舰标杆', '<p>华为Mate 60 Pro是华为最新的旗舰手机</p><ul><li>麒麟9000S芯片</li><li>卫星通话功能</li><li>昆仑玻璃</li></ul>', '{"存储":"256GB","颜色":"雅丹黑","屏幕":"6.82英寸"}', 1),
(7, '索尼 WH-1000XM5 耳机', 4, 2999.00, 3299.00, 60, 320, '["https://picsum.photos/400/400?random=7"]', '业界最佳降噪耳机，30小时续航，舒适佩戴', '<p>索尼WH-1000XM5是索尼最新旗舰降噪耳机</p><ul><li>业界最佳降噪</li><li>30小时续航</li><li>多点连接</li></ul>', '{"降噪":"是","续航":"30小时","重量":"250g"}', 1),
(8, 'iPad Pro 12.9英寸 M2', 3, 9299.00, 9999.00, 45, 180, '["https://picsum.photos/400/400?random=8"]', 'M2芯片，Liquid视网膜XDR显示屏，创作利器', '<p>iPad Pro 12.9英寸是最强大的iPad</p><ul><li>M2芯片</li><li>Liquid视网膜XDR显示屏</li><li>支持Apple Pencil 2代</li></ul>', '{"芯片":"M2","存储":"256GB","屏幕":"12.9英寸"}', 1);

SELECT '商品表更新完成!' AS message;
