-- 更新商品图片路径为本地静态资源
-- 图片存放在 src/main/resources/static/images/products/ 目录下
-- 应用启动时会自动从 picsum.photos 下载图片

USE online_mall;

-- 手机数码类
UPDATE product SET images = '["/images/products/product_1_1.jpg", "/images/products/product_1_2.jpg"]' WHERE id = 1;
UPDATE product SET images = '["/images/products/product_2_1.jpg", "/images/products/product_2_2.jpg"]' WHERE id = 2;
UPDATE product SET images = '["/images/products/product_3_1.jpg", "/images/products/product_3_2.jpg"]' WHERE id = 3;

-- 电脑办公类
UPDATE product SET images = '["/images/products/product_4_1.jpg", "/images/products/product_4_2.jpg"]' WHERE id = 4;
UPDATE product SET images = '["/images/products/product_5_1.jpg", "/images/products/product_5_2.jpg"]' WHERE id = 5;

-- 手机配件类
UPDATE product SET images = '["/images/products/product_6_1.jpg", "/images/products/product_6_2.jpg"]' WHERE id = 6;
UPDATE product SET images = '["/images/products/product_7_1.jpg", "/images/products/product_7_2.jpg"]' WHERE id = 7;

-- 家用电器类
UPDATE product SET images = '["/images/products/product_8_1.jpg", "/images/products/product_8_2.jpg"]' WHERE id = 8;
UPDATE product SET images = '["/images/products/product_9_1.jpg", "/images/products/product_9_2.jpg"]' WHERE id = 9;
UPDATE product SET images = '["/images/products/product_10_1.jpg", "/images/products/product_10_2.jpg"]' WHERE id = 10;

-- 电脑配件类
UPDATE product SET images = '["/images/products/product_11_1.jpg", "/images/products/product_11_2.jpg"]' WHERE id = 11;
UPDATE product SET images = '["/images/products/product_12_1.jpg", "/images/products/product_12_2.jpg"]' WHERE id = 12;

-- 查看更新结果
SELECT id, name, images FROM product;
