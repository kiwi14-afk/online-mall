package com.mall.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import javax.annotation.PostConstruct;

/**
 * 网关全局配置
 */
@Configuration
public class GatewayConfig {

    /**
     * 跨域配置（允许前端 Vue 开发时的跨域请求）
     */
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }

    /**
     * Sentinel 限流后的自定义返回
     */
    @PostConstruct
    public void initSentinelFallback() {
        GatewayCallbackManager.setBlockHandler((exchange, t) -> {
            String json = "{\"code\":429,\"message\":\"请求过于频繁，请稍后重试\",\"data\":null}";
            exchange.getResponse().getHeaders().set("Content-Type", "application/json;charset=UTF-8");
            return exchange.getResponse()
                    .writeWith(reactor.core.publisher.Mono.just(
                            exchange.getResponse().bufferFactory().wrap(json.getBytes())
                    ));
        });
    }

    /**
     * IP 限流 KeyResolver
     */
    @Bean(name = "ipKeyResolver")
    public org.springframework.cloud.gateway.filter.ratelimit.KeyResolver ipKeyResolver() {
        return exchange -> reactor.core.publisher.Mono.just(
                exchange.getRequest().getRemoteAddress() != null
                        ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                        : "unknown"
        );
    }
}
