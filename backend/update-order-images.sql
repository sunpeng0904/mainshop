-- 更新订单商品图片路径为本地静态资源
-- 图片存放在 src/main/resources/static/images/products/ 目录下

USE online_mall;

-- 更新 order_item 表中的 product_image 字段
-- 根据商品ID关联更新图片

UPDATE order_item oi
JOIN product p ON oi.product_id = p.id
SET oi.product_image = JSON_UNQUOTE(JSON_EXTRACT(p.images, '$[0]'))
WHERE oi.product_id IS NOT NULL;

-- 查看更新结果
SELECT oi.id, oi.order_id, oi.product_name, oi.product_image
FROM order_item oi
ORDER BY oi.order_id DESC
LIMIT 20;
