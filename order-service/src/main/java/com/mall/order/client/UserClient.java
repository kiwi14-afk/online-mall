package com.mall.order.client;

import com.mall.order.dto.Result;
import com.mall.order.client.fallback.UserClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

/**
 * 调用 user-service 的 Feign 客户端
 */
@FeignClient(name = "user-service", path = "/api/user", fallback = UserClientFallback.class)
public interface UserClient {

    /**
     * 验证用户是否存在（对应 user-service 的 /api/user/validate/{userId}）
     */
    @GetMapping("/validate/{id}")
    Result<Boolean> checkUserExists(@PathVariable("id") Long userId);

    /**
     * 获取用户基本信息
     */
    @GetMapping("/profile")
    Result<Map<String, Object>> getProfile(@RequestHeader("X-User-Id") String userId);
}
