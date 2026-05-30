-- 内幕信息知情人登记表
CREATE TABLE IF NOT EXISTS `t_insider_info` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `company_name` VARCHAR(200) NOT NULL COMMENT '企业名称',
    `accept_time` DATE COMMENT '受理时间',
    `board` VARCHAR(50) COMMENT '所属板块',
    `financing_type` VARCHAR(100) COMMENT '融资类型',
    `industry` VARCHAR(100) COMMENT '证监会行业细分',
    `knowledge_time` DATE COMMENT '知情日期',
    `reason` VARCHAR(500) COMMENT '理由',
    `content` VARCHAR(1000) COMMENT '知情内容',
    `insider_name` VARCHAR(50) COMMENT '知情人姓名',
    `register_time` DATE COMMENT '登记时间',
    `status` VARCHAR(30) DEFAULT 'draft' COMMENT '状态: draft-草稿, approved-审核通过, rejected-审核不通过, pending-待审核, submitted-已提交, approved_submitted-审核通过（已提交）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` INT DEFAULT 0 COMMENT '删除标志 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    INDEX `idx_company_name` (`company_name`),
    INDEX `idx_insider_name` (`insider_name`),
    INDEX `idx_status` (`status`),
    INDEX `idx_accept_time` (`accept_time`),
    INDEX `idx_knowledge_time` (`knowledge_time`),
    INDEX `idx_register_time` (`register_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内幕信息知情人登记表';

-- 插入测试数据
INSERT INTO `t_insider_info` (`company_name`, `accept_time`, `board`, `financing_type`, `industry`, `knowledge_time`, `reason`, `content`, `insider_name`, `register_time`, `status`) VALUES
('XXXXXXXXXXX有限公司', '2025-01-02', '主板（深市）', '首次公开发行股票', '制造业', '2025-01-02', '这里是理由内容', '针对XX项目....', '张鑫磊', '2025-01-02', 'draft'),
('XXXXXXXXXXX有限公司', '2025-01-02', '主板（沪市）', '向不特定对象募集股份', '制造业', '2025-01-02', '这里是理由内容', '针对XX项目....', '王磊', '2025-01-02', 'approved'),
('XXXXXXXXXXX有限公司', '2025-01-02', '主板（深市）', '向特定对象发行股票', '制造业', '2025-01-02', '这里是理由内容', '针对XX项目....', '张鑫磊', '2025-01-02', 'rejected'),
('XXXXXXXXXXX有限公司', '2025-01-02', '主板（沪市）', '首次公开发行股票', '制造业', '2025-01-02', '这里是理由内容', '针对XX项目....', '王磊', '2025-01-02', 'pending'),
('XXXXXXXXXXX有限公司', '2025-01-02', '科创板', '向不特定对象募集股份', '制造业', '2025-01-02', '这里是理由内容', '针对XX项目....', '张鑫磊', '2025-01-02', 'approved'),
('XXXXXXXXXXX有限公司', '2025-01-02', '创业板', '向特定对象发行股票', '制造业', '2025-01-02', '这里是理由内容', '针对XX项目....', '张鑫磊', '2025-01-02', 'rejected'),
('XXXXXXXXXXX有限公司', '2025-01-02', '创业板', '首次公开发行股票', '制造业', '2025-01-02', '这里是理由内容', '针对XX项目....', '王磊', '2025-01-02', 'submitted'),
('XXXXXXXXXXX有限公司', '2025-01-02', '科创板', '向不特定对象募集股份', '制造业', '2025-01-02', '这里是理由内容', '针对XX项目....', '张鑫磊', '2025-01-02', 'approved_submitted'),
('XXXXXXXXXXX有限公司', '2025-01-02', '创业板', '向特定对象发行股票', '制造业', '2025-01-02', '这里是理由内容', '针对XX项目....', '王磊', '2025-01-02', 'rejected'),
('XXXXXXXXXXX有限公司', '2025-01-02', '主板（沪市）', '首次公开发行股票', '制造业', '2025-01-02', '这里是理由内容', '针对XX项目....', '张鑫磊', '2025-01-02', 'pending'),
('XXXXXXXXXXX有限公司', '2025-01-02', '创业板', '向不特定对象募集股份', '制造业', '2025-01-02', '这里是理由内容', '针对XX项目....', '王磊', '2025-01-02', 'approved');
