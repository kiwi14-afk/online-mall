package com.mall.order.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 幂等性Token（防重复提交）
 */
@Data
@TableName("t_order_token")
public class OrderToken {

    private Long id;

    /** 幂等Token */
    private String token;

    /** 用户ID */
    private Long userId;

    /** 关联订单编号 */
    private String orderNo;

    /** 是否已使用 */
    private Integer used;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 过期时间 */
    private LocalDateTime expireTime;
}
