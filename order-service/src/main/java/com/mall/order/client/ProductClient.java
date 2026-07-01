package com.mall.order.client;

import com.mall.order.dto.Result;
import com.mall.order.client.fallback.ProductClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 调用 product-service 的 Feign 客户端
 */
@FeignClient(name = "product-service", path = "/api/product", fallback = ProductClientFallback.class)
public interface ProductClient {

    /**
     * 批量获取商品信息
     */
    @GetMapping("/batch")
    Result<List<Map<String, Object>>> getProductsByIds(@RequestParam("ids") List<Long> ids);

    /**
     * 获取单个商品信息
     */
    @GetMapping("/{id}")
    Result<Map<String, Object>> getProductById(@PathVariable("id") Long productId);

    /**
     * 扣减库存
     */
    @PutMapping("/{id}/stock/deduct")
    Result<Void> deductStock(@PathVariable("id") Long productId,
                             @RequestBody Map<String, Integer> request);

    /**
     * 回滚库存（事务补偿）
     */
    @PutMapping("/{id}/stock/rollback")
    Result<Void> rollbackStock(@PathVariable("id") Long productId,
                               @RequestBody Map<String, Integer> request);
}
