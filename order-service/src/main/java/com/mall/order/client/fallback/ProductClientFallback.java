package com.mall.order.client.fallback;

import com.mall.order.client.ProductClient;
import com.mall.order.dto.Result;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * ProductClient 降级处理（Sentinel）
 */
@Component
public class ProductClientFallback implements ProductClient {

    @Override
    public Result<List<Map<String, Object>>> getProductsByIds(List<Long> ids) {
        return Result.fail(503, "商品服务繁忙，请稍后重试");
    }

    @Override
    public Result<Map<String, Object>> getProductById(Long productId) {
        return Result.fail(503, "商品服务繁忙，请稍后重试");
    }

    @Override
    public Result<Void> deductStock(Long productId, Map<String, Integer> request) {
        return Result.fail(503, "商品服务繁忙，请稍后重试");
    }

    @Override
    public Result<Void> rollbackStock(Long productId, Map<String, Integer> request) {
        return Result.fail(503, "商品服务繁忙，请稍后重试");
    }
}
