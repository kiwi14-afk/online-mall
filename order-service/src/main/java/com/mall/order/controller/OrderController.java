package com.mall.order.controller;

import com.mall.order.dto.CreateOrderRequest;
import com.mall.order.dto.Result;
import com.mall.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

/**
 * 订单服务控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 创建订单（跨服务核心流程）
     * Feign 调用 user-service 验证用户 + product-service 扣库存
     * 幂等性：通过 orderToken 防重复提交
     * 分布式事务：Seata AT模式
     */
    @PostMapping
    public Result<Map<String, Object>> createOrder(@Valid @RequestBody CreateOrderRequest request,
                                                    HttpServletRequest httpRequest) {
        log.info("收到创建订单请求: token={}", request.getOrderToken());
        return orderService.createOrder(request, httpRequest);
    }

    /**
     * 查询用户订单列表（分页）
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> listOrders(@RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size,
                                                   @RequestParam(required = false) String status,
                                                   HttpServletRequest httpRequest) {
        return orderService.listOrders(page, size, status, httpRequest);
    }

    /**
     * 查询订单详情
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getOrderDetail(@PathVariable("id") Long orderId,
                                                       HttpServletRequest httpRequest) {
        return orderService.getOrderDetail(orderId, httpRequest);
    }

    /**
     * 取消订单（回滚库存）
     */
    @PutMapping("/{id}/cancel")
    public Result<Void> cancelOrder(@PathVariable("id") Long orderId,
                                     HttpServletRequest httpRequest) {
        return orderService.cancelOrder(orderId, httpRequest);
    }

    /**
     * 根据订单编号查询订单（供 payment-service Feign 内部调用）
     */
    @GetMapping("/no/{orderNo}")
    public Result<Map<String, Object>> getOrderByNo(@PathVariable String orderNo) {
        return orderService.getOrderByNo(orderNo);
    }
}
