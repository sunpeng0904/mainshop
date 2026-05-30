-- 用户表
CREATE TABLE IF NOT EXISTS t_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    avatar VARCHAR(500),
    nickname VARCHAR(50),
    gender INT DEFAULT 0,
    birthday TIMESTAMP,
    status INT DEFAULT 1,
    last_login_time TIMESTAMP,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_phone (phone)
);

-- 角色表
CREATE TABLE IF NOT EXISTS t_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_code VARCHAR(50) NOT NULL UNIQUE,
    role_name VARCHAR(50) NOT NULL,
    description VARCHAR(200),
    sort INT DEFAULT 0,
    status INT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    INDEX idx_role_code (role_code)
);

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS t_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_role (user_id, role_id),
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id)
);

-- 分类表
CREATE TABLE IF NOT EXISTS category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    parent_id BIGINT DEFAULT 0,
    level INT DEFAULT 1,
    sort INT DEFAULT 0,
    status INT DEFAULT 1,
    icon VARCHAR(50),
    description VARCHAR(200),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 商品表
CREATE TABLE IF NOT EXISTS product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    category_id BIGINT,
    price DECIMAL(10, 2) NOT NULL,
    original_price DECIMAL(10, 2),
    stock INT DEFAULT 0,
    sales INT DEFAULT 0,
    images TEXT,
    description TEXT,
    detail TEXT,
    specifications TEXT,
    status INT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    INDEX idx_category (category_id),
    INDEX idx_status (status)
);

-- 购物车表
CREATE TABLE IF NOT EXISTS cart (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT DEFAULT 1,
    selected INT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    INDEX idx_user (user_id),
    INDEX idx_product (product_id),
    UNIQUE KEY uk_user_product (user_id, product_id)
);

-- 订单表
CREATE TABLE IF NOT EXISTS t_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(50) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    status INT DEFAULT 0,
    shipping_address VARCHAR(500),
    shipping_company VARCHAR(100),
    shipping_number VARCHAR(100),
    remark VARCHAR(500),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    INDEX idx_user (user_id),
    INDEX idx_order_no (order_no),
    INDEX idx_status (status)
);

-- 订单项表（支持订单商品明细）
CREATE TABLE IF NOT EXISTS order_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    product_image VARCHAR(500),
    product_price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    specifications TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    INDEX idx_order (order_id),
    INDEX idx_product (product_id)
);

-- 用户地址表（符合ATTRC2E词根规范）
CREATE TABLE IF NOT EXISTS user_addr_tb (
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
    PRIMARY KEY (id),
    INDEX idx_user_addr_user_id (user_id),
    INDEX idx_user_addr_user_id_dft (user_id, dft_indc, entr_time)
);

-- 内幕信息知情人登记表
CREATE TABLE IF NOT EXISTS t_insider_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    company_name VARCHAR(200) NOT NULL,
    accept_time DATE,
    board VARCHAR(50),
    financing_type VARCHAR(100),
    industry VARCHAR(100),
    knowledge_time DATE,
    reason VARCHAR(500),
    content VARCHAR(1000),
    insider_name VARCHAR(50),
    register_time DATE,
    status VARCHAR(30) DEFAULT 'draft',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    INDEX idx_company_name (company_name),
    INDEX idx_insider_name (insider_name),
    INDEX idx_status (status),
    INDEX idx_accept_time (accept_time),
    INDEX idx_knowledge_time (knowledge_time),
    INDEX idx_register_time (register_time)
);