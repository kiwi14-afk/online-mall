package com.mall.payment.client;

import com.mall.payment.dto.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * 调用 order-service 的 Feign 客户端
 */
@FeignClient(name = "order-service", path = "/api/order")
public interface OrderClient {

    /**
     * 根据订单编号获取订单信息（供内部调用）
     */
    @GetMapping("/no/{orderNo}")
    Result<Map<String, Object>> getOrderByNo(@PathVariable("orderNo") String orderNo);
}
