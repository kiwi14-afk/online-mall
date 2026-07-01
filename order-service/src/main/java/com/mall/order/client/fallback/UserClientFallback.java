package com.mall.order.client.fallback;

import com.mall.order.client.UserClient;
import com.mall.order.dto.Result;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * UserClient 降级处理（Sentinel）
 */
@Component
public class UserClientFallback implements UserClient {

    @Override
    public Result<Boolean> checkUserExists(Long userId) {
        return Result.fail(503, "用户服务繁忙，请稍后重试");
    }

    @Override
    public Result<Map<String, Object>> getProfile(String userId) {
        return Result.fail(503, "用户服务繁忙，请稍后重试");
    }
}
