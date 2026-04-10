-- 更新商品图片路径
-- 图片存放在 src/main/resources/static/images/products/ 目录下

USE online_mall;

-- 更新商品图片路径为项目内的静态资源路径
UPDATE product SET images = '["/images/products/iphone15-promax.jpg"]' WHERE id = 1;
UPDATE product SET images = '["/images/products/xiaomi14-ultra.jpg"]' WHERE id = 2;
UPDATE product SET images = '["/images/products/macbook-pro14.jpg"]' WHERE id = 3;
UPDATE product SET images = '["/images/products/dell-xps15.jpg"]' WHERE id = 4;
UPDATE product SET images = '["/images/products/dyson-v15.jpg"]' WHERE id = 5;

-- 查看更新结果
SELECT id, name, images FROM product;
