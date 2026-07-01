package com.mall.payment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付实体
 */
@Data
@TableName("t_payment")
public class Payment {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 支付流水号 */
    private String paymentNo;

    /** 关联订单编号 */
    private String orderNo;

    /** 用户ID */
    private Long userId;

    /** 支付金额 */
    private BigDecimal amount;

    /** 支付状态: UNPAID/PAID/REFUNDING/REFUNDED */
    private String status;

    /** 支付方式: WECHAT/ALIPAY/CARD */
    private String payMethod;

    /** 支付时间 */
    private LocalDateTime payTime;

    /** 退款时间 */
    private LocalDateTime refundTime;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
