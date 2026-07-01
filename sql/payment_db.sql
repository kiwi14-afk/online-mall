-- ============================================
-- payment_db: 支付服务数据库
-- ============================================

CREATE DATABASE IF NOT EXISTS payment_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE payment_db;

-- 支付记录表
DROP TABLE IF EXISTS t_payment;
CREATE TABLE t_payment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '支付ID',
    payment_no VARCHAR(64) NOT NULL UNIQUE COMMENT '支付流水号',
    order_no VARCHAR(64) NOT NULL COMMENT '关联订单编号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    amount DECIMAL(12,2) NOT NULL COMMENT '支付金额',
    status VARCHAR(20) NOT NULL DEFAULT 'UNPAID' COMMENT '支付状态: UNPAID/PAID/REFUNDING/REFUNDED',
    pay_method VARCHAR(20) DEFAULT 'WECHAT' COMMENT '支付方式: WECHAT/ALIPAY/CARD',
    pay_time DATETIME COMMENT '支付时间',
    refund_time DATETIME COMMENT '退款时间',
    remark VARCHAR(255) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_order_no (order_no),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支付记录表';

-- Seata AT模式所需undo_log表
CREATE TABLE IF NOT EXISTS undo_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    branch_id BIGINT NOT NULL,
    xid VARCHAR(128) NOT NULL,
    context VARCHAR(128) NOT NULL,
    rollback_info LONGBLOB NOT NULL,
    log_status INT NOT NULL,
    log_created DATETIME NOT NULL,
    log_modified DATETIME NOT NULL,
    UNIQUE KEY ux_undo_log (xid, branch_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Seata AT undo_log';
