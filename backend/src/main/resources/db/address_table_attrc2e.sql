-- 地址管理模块表结构（符合ATTRC2E词根规范）
-- 生成时间：2026-05-29
-- 词根依据：ATTRC2E.txt

-- ============================================
-- 1. 用户地址表 (user_addr_tb)
-- ============================================

CREATE TABLE user_addr_tb (
    id VARCHAR(32) NOT NULL,
    user_id VARCHAR(32) NULL COMMENT '用户标识',
    rcvr_name VARCHAR(100) NULL COMMENT '收货人姓名',
    rcvr_tel VARCHAR(20) NULL COMMENT '收货人电话',
    prvc_cde VARCHAR(10) NULL COMMENT '省份编码',
    city_cde VARCHAR(10) NULL COMMENT '城市编码',
    dstrct_cde VARCHAR(10) NULL COMMENT '区县编码',
    dtl_addr VARCHAR(500) NULL COMMENT '详细地址',
    dft_indc CHAR(1) DEFAULT 'N' NULL COMMENT '是否默认标志 Y-是 N-否',
    entr_psn_id VARCHAR(32) NULL COMMENT '创建人标识',
    entr_psn_name VARCHAR(100) NULL COMMENT '创建人姓名',
    entr_time TIMESTAMP NULL COMMENT '创建时间',
    last_alter_psn_id VARCHAR(32) NULL COMMENT '最后修改人标识',
    last_alter_psn_name VARCHAR(100) NULL COMMENT '最后修改人姓名',
    last_alter_time TIMESTAMP NULL COMMENT '最后修改时间',
    vld_sts_cde CHAR(1) DEFAULT 'N' NULL COMMENT '删除标志 Y-是 N-否',
    PRIMARY KEY (id)
);

-- 索引
CREATE INDEX idx_user_addr_user_id ON user_addr_tb(user_id);
CREATE INDEX idx_user_addr_user_id_dft ON user_addr_tb(user_id, dft_indc, entr_time);

-- 表注释
COMMENT ON TABLE user_addr_tb IS '用户地址表';

-- 列注释
COMMENT ON COLUMN user_addr_tb.id IS '主键标识';
COMMENT ON COLUMN user_addr_tb.user_id IS '用户标识';
COMMENT ON COLUMN user_addr_tb.rcvr_name IS '收货人姓名';
COMMENT ON COLUMN user_addr_tb.rcvr_tel IS '收货人电话';
COMMENT ON COLUMN user_addr_tb.prvc_cde IS '省份编码';
COMMENT ON COLUMN user_addr_tb.city_cde IS '城市编码';
COMMENT ON COLUMN user_addr_tb.dstrct_cde IS '区县编码';
COMMENT ON COLUMN user_addr_tb.dtl_addr IS '详细地址';
COMMENT ON COLUMN user_addr_tb.dft_indc IS '是否默认标志';
COMMENT ON COLUMN user_addr_tb.entr_psn_id IS '创建人标识';
COMMENT ON COLUMN user_addr_tb.entr_psn_name IS '创建人姓名';
COMMENT ON COLUMN user_addr_tb.entr_time IS '创建时间';
COMMENT ON COLUMN user_addr_tb.last_alter_psn_id IS '最后修改人标识';
COMMENT ON COLUMN user_addr_tb.last_alter_psn_name IS '最后修改人姓名';
COMMENT ON COLUMN user_addr_tb.last_alter_time IS '最后修改时间';
COMMENT ON COLUMN user_addr_tb.vld_sts_cde IS '删除标志';

-- ============================================
-- 2. 区域表 (region_tb)
-- ============================================

CREATE TABLE region_tb (
    id VARCHAR(32) NOT NULL,
    cde VARCHAR(10) NOT NULL COMMENT '区域编码',
    name VARCHAR(100) NOT NULL COMMENT '区域名称',
    prnt_cde VARCHAR(10) NULL COMMENT '父级编码',
    lvl INTEGER NULL COMMENT '级别 1-省 2-市 3-区',
    entr_psn_id VARCHAR(32) NULL COMMENT '创建人标识',
    entr_psn_name VARCHAR(100) NULL COMMENT '创建人姓名',
    entr_time TIMESTAMP NULL COMMENT '创建时间',
    last_alter_psn_id VARCHAR(32) NULL COMMENT '最后修改人标识',
    last_alter_psn_name VARCHAR(100) NULL COMMENT '最后修改人姓名',
    last_alter_time TIMESTAMP NULL COMMENT '最后修改时间',
    vld_sts_cde CHAR(1) DEFAULT 'N' NULL COMMENT '删除标志 Y-是 N-否',
    PRIMARY KEY (id)
);

-- 索引
CREATE INDEX idx_region_cde ON region_tb(cde);
CREATE INDEX idx_region_prnt_cde ON region_tb(prnt_cde);

-- 表注释
COMMENT ON TABLE region_tb IS '区域表';

-- 列注释
COMMENT ON COLUMN region_tb.id IS '主键标识';
COMMENT ON COLUMN region_tb.cde IS '区域编码';
COMMENT ON COLUMN region_tb.name IS '区域名称';
COMMENT ON COLUMN region_tb.prnt_cde IS '父级编码';
COMMENT ON COLUMN region_tb.lvl IS '级别';
COMMENT ON COLUMN region_tb.entr_psn_id IS '创建人标识';
COMMENT ON COLUMN region_tb.entr_psn_name IS '创建人姓名';
COMMENT ON COLUMN region_tb.entr_time IS '创建时间';
COMMENT ON COLUMN region_tb.last_alter_psn_id IS '最后修改人标识';
COMMENT ON COLUMN region_tb.last_alter_psn_name IS '最后修改人姓名';
COMMENT ON COLUMN region_tb.last_alter_time IS '最后修改时间';
COMMENT ON COLUMN region_tb.vld_sts_cde IS '删除标志';
