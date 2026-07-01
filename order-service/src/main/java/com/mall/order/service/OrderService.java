package com.mall.order.service;

import com.mall.order.dto.CreateOrderRequest;
import com.mall.order.dto.Result;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 订单服务接口
 */
public interface OrderService {

    /**
     * 创建订单（跨服务：验证用户 + 扣库存）
     * 使用 @GlobalTransactional 保证分布式事务一致性
     */
    Result<Map<String, Object>> createOrder(CreateOrderRequest request, HttpServletRequest httpRequest);

    /**
     * 查询用户订单列表（分页）
     */
    Result<Map<String, Object>> listOrders(Integer page, Integer size, String status, HttpServletRequest httpRequest);

    /**
     * 查询订单详情
     */
    Result<Map<String, Object>> getOrderDetail(Long orderId, HttpServletRequest httpRequest);

    /**
     * 取消订单（回滚库存）
     */
    Result<Void> cancelOrder(Long orderId, HttpServletRequest httpRequest);

    /**
     * 根据订单编号查询订单（供内部 Feign 调用，无需鉴权）
     */
    Result<Map<String, Object>> getOrderByNo(String orderNo);
}
