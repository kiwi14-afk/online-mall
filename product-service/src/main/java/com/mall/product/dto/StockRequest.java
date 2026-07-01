package com.mall.product.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class StockRequest {

    @NotNull(message = "扣减数量不能为空")
    @Positive(message = "扣减数量必须大于0")
    private Integer quantity;
}
