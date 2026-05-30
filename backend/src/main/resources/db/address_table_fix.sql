-- 地址管理模块表字段修正脚本
-- 目的：使表字段符合词根字典规范
-- 执行时间：2026-05-29

-- ============================================
-- 1. 用户地址表字段修正
-- ============================================

-- 1.1 修改删除标志字段：deleted (int) -> del_flag (char)
ALTER TABLE user_address CHANGE COLUMN deleted del_flag CHAR(1) DEFAULT 'N' COMMENT '删除标志 Y-是 N-否';

-- 1.2 添加创建人字段
ALTER TABLE user_address ADD COLUMN create_by VARCHAR(50) DEFAULT 'system' COMMENT '创建人';

-- 1.3 添加更新人字段
ALTER TABLE user_address ADD COLUMN update_by VARCHAR(50) DEFAULT 'system' COMMENT '更新人';

-- ============================================
-- 2. 区域表字段修正（如果存在）
-- ============================================

-- 2.1 修改删除标志字段
ALTER TABLE region CHANGE COLUMN deleted del_flag CHAR(1) DEFAULT 'N' COMMENT '删除标志 Y-是 N-否';

-- 2.2 添加创建人字段
ALTER TABLE region ADD COLUMN create_by VARCHAR(50) DEFAULT 'system' COMMENT '创建人';

-- 2.3 添加更新人字段
ALTER TABLE region ADD COLUMN update_by VARCHAR(50) DEFAULT 'system' COMMENT '更新人';

-- ============================================
-- 3. 更新现有数据
-- ============================================

-- 3.1 将 deleted=0 转换为 del_flag='N'，deleted=1 转换为 del_flag='Y'
UPDATE user_address SET del_flag = 'N' WHERE del_flag = '0' OR del_flag IS NULL;
UPDATE user_address SET del_flag = 'Y' WHERE del_flag = '1';

-- ============================================
-- 4. 验证修正结果
-- ============================================

-- 验证 user_address 表结构
-- DESC user_address;

-- 验证数据
-- SELECT id, user_id, receiver_name, del_flag, create_by, update_by FROM user_address LIMIT 5;
