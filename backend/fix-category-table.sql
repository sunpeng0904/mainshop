-- 修复 category 表缺少 description 字段的问题
-- 执行时间: 2026-04-09

USE online_mall;

-- 添加 description 字段
ALTER TABLE category ADD COLUMN description VARCHAR(500) COMMENT '分类描述' AFTER icon;
