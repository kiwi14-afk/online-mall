-- ============================================
-- product_db: 商品服务数据库（Person A）
-- ============================================

CREATE DATABASE IF NOT EXISTS product_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE product_db;

-- 商品分类表
DROP TABLE IF EXISTS t_category;
CREATE TABLE t_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID',
    name VARCHAR(64) NOT NULL UNIQUE COMMENT '分类名称',
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品分类表';

-- 商品表
DROP TABLE IF EXISTS t_product;
CREATE TABLE t_product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '商品ID',
    name VARCHAR(200) NOT NULL COMMENT '商品名称',
    description TEXT COMMENT '商品描述',
    price DECIMAL(10,2) NOT NULL COMMENT '商品价格',
    stock INT NOT NULL DEFAULT 0 COMMENT '库存数量',
    image_url VARCHAR(500) DEFAULT '' COMMENT '商品图片URL',
    category_id BIGINT COMMENT '分类ID',
    status INT NOT NULL DEFAULT 1 COMMENT '状态: 1-上架, 0-下架',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_category_id (category_id),
    INDEX idx_status (status),
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES t_category(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';

-- 插入默认分类
INSERT INTO t_category (name) VALUES ('手机数码'), ('电脑办公'), ('家用电器'), ('服装鞋帽'), ('食品生鲜'), ('图书音像');
