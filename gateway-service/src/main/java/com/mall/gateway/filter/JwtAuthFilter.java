package com.mall.gateway.filter;

import com.mall.gateway.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * JWT 鉴权全局过滤器
 * 检查请求头中的 Authorization，放行白名单路径
 */
@Slf4j
@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${auth.exclude-paths}")
    private List<String> excludePaths;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 1. 检查是否在白名单中
        if (isExcluded(exchange)) {
            log.debug("白名单路径，跳过鉴权: {}", path);
            return chain.filter(exchange);
        }

        // 2. 获取 Token
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("缺少 Authorization 头: {}", path);
            return unauthorized(exchange, "未提供Token或Token格式错误");
        }

        String token = authHeader.substring(7);

        // 3. 校验 Token
        if (!jwtUtil.validateToken(token)) {
            log.warn("Token校验失败: {}", path);
            return unauthorized(exchange, "Token无效或已过期");
        }

        // 4. 解析用户信息，透传到下游服务
        Long userId = jwtUtil.getUserId(token);
        String username = jwtUtil.getUsername(token);

        if (userId == null) {
            log.warn("Token中未包含用户ID: {}", path);
            return unauthorized(exchange, "Token格式错误");
        }

        // 5. 添加自定义Header，传递用户信息给下游服务
        ServerHttpRequest modifiedRequest = request.mutate()
                .header("X-User-Id", String.valueOf(userId))
                .header("X-Username", username)
                .build();

        log.debug("JWT鉴权通过: userId={}, username={}, path={}", userId, username, path);

        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }

    @Override
    public int getOrder() {
        return -100; // 高优先级，最早执行
    }

    /**
     * 判断路径是否在白名单中
     * 支持 "METHOD:/path" 格式进行方法级过滤，也支持纯路径匹配
     */
    private boolean isExcluded(ServerWebExchange exchange) {
        if (excludePaths == null) return false;
        String path = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethodValue();

        for (String pattern : excludePaths) {
            // 支持 "GET:/api/xxx/*" 格式的方法级白名单
            if (pattern.contains(":/")) {
                int idx = pattern.indexOf(":/");
                String requiredMethod = pattern.substring(0, idx);
                String pathPattern = pattern.substring(idx + 1);
                if (method.equalsIgnoreCase(requiredMethod) && pathMatcher.match(pathPattern, path)) {
                    return true;
                }
            } else {
                // 纯路径匹配（不限制方法）
                if (pathMatcher.match(pattern, path)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 返回 401 未授权响应
     */
    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = String.format("{\"code\":401,\"message\":\"%s\",\"data\":null}", message);
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}
