-- 抽奖活动表结构
-- 在MySQL中执行以下SQL

-- 奖品表
CREATE TABLE IF NOT EXISTS `lottery_prize` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '奖品ID',
  `name` varchar(100) NOT NULL COMMENT '奖品名称',
  `level` int NOT NULL COMMENT '奖品等级 1-一等奖 2-二等奖 3-三等奖 4-谢谢参与',
  `image` varchar(255) DEFAULT NULL COMMENT '奖品图片',
  `value` decimal(10,2) DEFAULT '0.00' COMMENT '奖品价值',
  `probability` decimal(5,2) NOT NULL COMMENT '中奖概率（百分比）',
  `stock` int NOT NULL DEFAULT '0' COMMENT '库存数量',
  `issued` int NOT NULL DEFAULT '0' COMMENT '已发放数量',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态 0-禁用 1-启用',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
  `version` int NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='抽奖奖品表';

-- 抽奖记录表
CREATE TABLE IF NOT EXISTS `lottery_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `username` varchar(50) DEFAULT NULL COMMENT '用户名',
  `prize_id` bigint NOT NULL COMMENT '奖品ID',
  `prize_name` varchar(100) NOT NULL COMMENT '奖品名称',
  `prize_level` int NOT NULL COMMENT '奖品等级',
  `lottery_time` datetime NOT NULL COMMENT '抽奖时间',
  `receive_status` int NOT NULL DEFAULT '0' COMMENT '领取状态 0-未领取 1-已领取',
  `receive_time` datetime DEFAULT NULL COMMENT '领取时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_lottery_time` (`lottery_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='抽奖记录表';

-- 初始化奖品数据
INSERT INTO `lottery_prize` (`name`, `level`, `value`, `probability`, `stock`, `status`, `sort`) VALUES
('笔记本电脑', 1, 5000.00, 0.10, 10, 1, 1),
('智能手机', 2, 2000.00, 1.00, 50, 1, 2),
('保温杯', 3, 100.00, 5.00, 500, 1, 3),
('谢谢参与', 4, 0.00, 93.90, 999999, 1, 4);
