package com.mall.order.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单项实体
 */
@Data
@TableName("t_order_item")
public class OrderItem {

    private Long id;

    /** 订单ID */
    private Long orderId;

    /** 订单编号 */
    private String orderNo;

    /** 商品ID */
    private Long productId;

    /** 商品名称 */
    private String productName;

    /** 商品单价 */
    private BigDecimal productPrice;

    /** 购买数量 */
    private Integer quantity;

    /** 小计金额 */
    private BigDecimal totalPrice;

    /** 创建时间 */
    private LocalDateTime createTime;
}
