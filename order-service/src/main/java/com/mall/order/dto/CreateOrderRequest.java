package com.mall.order.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * 创建订单请求
 */
@Data
public class CreateOrderRequest {

    /** 幂等性Token（防重复提交） */
    @NotBlank(message = "订单Token不能为空")
    private String orderToken;

    /** 收货人姓名 */
    @NotBlank(message = "收货人姓名不能为空")
    private String receiverName;

    /** 收货人电话 */
    @NotBlank(message = "收货人电话不能为空")
    private String receiverPhone;

    /** 收货地址 */
    @NotBlank(message = "收货地址不能为空")
    private String receiverAddress;

    /** 订单项列表 */
    @NotEmpty(message = "订单项不能为空")
    private List<OrderItemRequest> items;

    @Data
    public static class OrderItemRequest {
        @NotNull(message = "商品ID不能为空")
        private Long productId;

        @NotNull(message = "数量不能为空")
        private Integer quantity;
    }
}
