-- 地址管理模块优化SQL脚本
-- 执行时间：2026-05-29

-- 1. 添加索引优化查询性能
-- 用户地址表添加复合索引
CREATE INDEX idx_user_address_user_id_default_time ON user_address(user_id, is_default, create_time);

-- 2. 添加区域表索引（如果不存在）
CREATE INDEX IF NOT EXISTS idx_region_code ON region(code);
CREATE INDEX IF NOT EXISTS idx_region_parent_code ON region(parent_code);

-- 3. 优化查询语句示例
-- 获取用户地址列表（使用索引）
-- SELECT * FROM user_address
-- WHERE user_id = ? AND deleted = 0
-- ORDER BY is_default DESC, create_time DESC;

-- 获取默认地址（使用索引）
-- SELECT * FROM user_address
-- WHERE user_id = ? AND is_default = 1 AND deleted = 0
-- LIMIT 1;

-- 4. 添加地址数量限制配置表（可选）
-- CREATE TABLE IF NOT EXISTS sys_config (
--     id BIGINT PRIMARY KEY AUTO_INCREMENT,
--     config_key VARCHAR(100) NOT NULL COMMENT '配置键',
--     config_value VARCHAR(500) COMMENT '配置值',
--     description VARCHAR(200) COMMENT '描述',
--     create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
--     update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
--     UNIQUE KEY uk_config_key (config_key)
-- ) COMMENT '系统配置表';

-- INSERT INTO sys_config (config_key, config_value, description) VALUES
-- ('address.max.count', '20', '用户最大地址数量');
