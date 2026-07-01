package com.mall.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.order.client.ProductClient;
import com.mall.order.client.UserClient;
import com.mall.order.dto.CreateOrderRequest;
import com.mall.order.dto.Result;
import com.mall.order.entity.Order;
import com.mall.order.entity.OrderItem;
import com.mall.order.mapper.OrderItemMapper;
import com.mall.order.mapper.OrderMapper;
import com.mall.order.mapper.OrderTokenMapper;
import com.mall.order.service.OrderService;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 订单服务实现
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private OrderTokenMapper orderTokenMapper;

    @Autowired
    private UserClient userClient;

    @Autowired
    private ProductClient productClient;

    @Override
    @GlobalTransactional(name = "create-order-tx", rollbackFor = Exception.class)
    @Transactional(rollbackFor = Exception.class)
    public Result<Map<String, Object>> createOrder(CreateOrderRequest request, HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);

        // 1. 幂等性校验：检查订单Token是否有效
        String orderToken = request.getOrderToken();
        String orderNo = generateOrderNo();
        int tokenResult = orderTokenMapper.useToken(orderToken, orderNo);
        if (tokenResult == 0) {
            log.warn("订单Token无效或已使用: {}", orderToken);
            return Result.fail(400, "订单已提交或Token无效，请勿重复提交");
        }

        log.info("开始创建订单, userId={}, orderNo={}, token={}", userId, orderNo, orderToken);

        // 2. Feign 调用 user-service 验证用户是否存在
        try {
            Result<Boolean> userResult = userClient.checkUserExists(userId);
            if (!userResult.isSuccess() || !Boolean.TRUE.equals(userResult.getData())) {
                log.error("用户验证失败: userId={}", userId);
                return Result.fail(400, "用户不存在或验证失败");
            }
        } catch (Exception e) {
            log.error("调用用户服务失败: {}", e.getMessage());
            return Result.fail(503, "用户服务繁忙，请稍后重试");
        }

        // 3. 收集商品ID列表
        List<Long> productIds = request.getItems().stream()
                .map(CreateOrderRequest.OrderItemRequest::getProductId)
                .collect(Collectors.toList());

        // 4. Feign 调用 product-service 批量获取商品信息
        Map<Long, Map<String, Object>> productMap;
        try {
            Result<List<Map<String, Object>>> productResult = productClient.getProductsByIds(productIds);
            if (!productResult.isSuccess() || productResult.getData() == null) {
                return Result.fail(400, "获取商品信息失败");
            }
            productMap = productResult.getData().stream()
                    .collect(Collectors.toMap(
                            p -> ((Number) p.get("id")).longValue(),
                            p -> p
                    ));
        } catch (Exception e) {
            log.error("调用商品服务失败: {}", e.getMessage());
            return Result.fail(503, "商品服务繁忙，请稍后重试");
        }

        // 5. Feign 调用 product-service 逐项扣减库存
        for (CreateOrderRequest.OrderItemRequest itemReq : request.getItems()) {
            Map<String, Object> productInfo = productMap.get(itemReq.getProductId());
            if (productInfo == null) {
                return Result.fail(400, "商品ID=" + itemReq.getProductId() + " 不存在");
            }
            Integer stock = productInfo.get("stock") != null
                    ? ((Number) productInfo.get("stock")).intValue() : 0;
            if (stock < itemReq.getQuantity()) {
                log.warn("库存不足: productId={}, stock={}, required={}",
                        itemReq.getProductId(), stock, itemReq.getQuantity());
                return Result.fail(400, "商品 [" + productInfo.get("name") + "] 库存不足");
            }
            try {
                Map<String, Integer> stockRequest = new HashMap<>();
                stockRequest.put("quantity", itemReq.getQuantity());
                Result<Void> deductResult = productClient.deductStock(itemReq.getProductId(), stockRequest);
                if (!deductResult.isSuccess()) {
                    log.error("扣减库存失败: productId={}, quantity={}", itemReq.getProductId(), itemReq.getQuantity());
                    throw new RuntimeException("扣减库存失败: productId=" + itemReq.getProductId());
                }
                log.info("库存扣减成功: productId={}, quantity={}", itemReq.getProductId(), itemReq.getQuantity());
            } catch (Exception e) {
                log.error("调用商品服务扣库存失败: {}", e.getMessage());
                return Result.fail(503, "商品服务繁忙，请稍后重试");
            }
        }

        // 6. 构建订单项列表（使用扣减前获取的商品信息）
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CreateOrderRequest.OrderItemRequest itemReq : request.getItems()) {
            Map<String, Object> productInfo = productMap.get(itemReq.getProductId());

            BigDecimal price = new BigDecimal(productInfo.get("price").toString());
            String productName = (String) productInfo.get("name");
            Integer quantity = itemReq.getQuantity();
            BigDecimal itemTotal = price.multiply(BigDecimal.valueOf(quantity));

            OrderItem item = new OrderItem();
            item.setOrderNo(orderNo);
            item.setProductId(itemReq.getProductId());
            item.setProductName(productName);
            item.setProductPrice(price);
            item.setQuantity(quantity);
            item.setTotalPrice(itemTotal);
            item.setCreateTime(LocalDateTime.now());
            orderItems.add(item);

            totalAmount = totalAmount.add(itemTotal);
        }

        // 7. 创建订单
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setStatus("PENDING");
        order.setReceiverName(request.getReceiverName());
        order.setReceiverPhone(request.getReceiverPhone());
        order.setReceiverAddress(request.getReceiverAddress());
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.insert(order);

        // 8. 保存订单项
        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
            orderItemMapper.insert(item);
        }

        log.info("订单创建成功: orderNo={}, userId={}, totalAmount={}", orderNo, userId, totalAmount);

        // 9. 构建返回结果
        Map<String, Object> resultData = new HashMap<>();
        resultData.put("orderId", order.getId());
        resultData.put("orderNo", orderNo);
        resultData.put("totalAmount", totalAmount);
        resultData.put("status", "PENDING");
        resultData.put("items", orderItems);

        return Result.success(resultData);
    }

    @Override
    public Result<Map<String, Object>> listOrders(Integer page, Integer size, String status, HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId);
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreateTime);

        Page<Order> pageResult = orderMapper.selectPage(new Page<>(page != null ? page : 1, size != null ? size : 10), wrapper);

        Map<String, Object> resultData = new HashMap<>();
        resultData.put("total", pageResult.getTotal());
        resultData.put("records", pageResult.getRecords());
        return Result.success(resultData);
    }

    @Override
    public Result<Map<String, Object>> getOrderDetail(Long orderId, HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);

        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return Result.fail(404, "订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            return Result.fail(403, "无权查看该订单");
        }

        // 查询订单项
        LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(OrderItem::getOrderNo, order.getOrderNo());
        List<OrderItem> items = orderItemMapper.selectList(itemWrapper);

        Map<String, Object> resultData = new HashMap<>();
        resultData.put("order", order);
        resultData.put("items", items);
        return Result.success(resultData);
    }

    @Override
    @GlobalTransactional(name = "cancel-order-tx", rollbackFor = Exception.class)
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> cancelOrder(Long orderId, HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);

        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return Result.fail(404, "订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            return Result.fail(403, "无权取消该订单");
        }
        if (!"PENDING".equals(order.getStatus())) {
            return Result.fail(400, "只有待支付订单才能取消");
        }

        // 回滚库存 - 调用 product-service
        LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(OrderItem::getOrderNo, order.getOrderNo());
        List<OrderItem> items = orderItemMapper.selectList(itemWrapper);

        for (OrderItem item : items) {
            try {
                Map<String, Integer> stockRequest = new HashMap<>();
                stockRequest.put("quantity", item.getQuantity());
                Result<Void> result = productClient.rollbackStock(item.getProductId(), stockRequest);
                if (!result.isSuccess()) {
                    log.error("库存回滚失败: productId={}, quantity={}", item.getProductId(), item.getQuantity());
                    throw new RuntimeException("库存回滚失败");
                }
                log.info("库存回滚成功: productId={}, quantity={}", item.getProductId(), item.getQuantity());
            } catch (Exception e) {
                log.error("调用商品服务回滚库存失败: {}", e.getMessage());
                return Result.fail(503, "商品服务繁忙，取消失败");
            }
        }

        // 更新订单状态
        order.setStatus("CANCELLED");
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);

        log.info("订单取消成功: orderNo={}, userId={}", order.getOrderNo(), userId);
        return Result.success();
    }

    @Override
    public Result<Map<String, Object>> getOrderByNo(String orderNo) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderNo, orderNo);
        Order order = orderMapper.selectOne(wrapper);

        if (order == null) {
            return Result.fail(404, "订单不存在");
        }

        // 查询订单项
        LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(OrderItem::getOrderNo, orderNo);
        List<OrderItem> items = orderItemMapper.selectList(itemWrapper);

        Map<String, Object> resultData = new HashMap<>();
        resultData.put("order", order);
        resultData.put("items", items);
        return Result.success(resultData);
    }

    // ==================== 工具方法 ====================

    /**
     * 生成订单编号: 时间戳 + 8位随机数字
     */
    private String generateOrderNo() {
        return System.currentTimeMillis() + String.format("%08d", new Random().nextInt(100000000));
    }

    /**
     * 从请求中获取用户ID
     * 实际生产环境应从 JWT 中解析，此处简化处理：从 Gateway 透传的 Header 中获取
     */
    private Long getUserIdFromRequest(HttpServletRequest request) {
        String userIdStr = request.getHeader("X-User-Id");
        if (userIdStr == null) {
            throw new RuntimeException("未获取到用户信息");
        }
        return Long.valueOf(userIdStr);
    }
}
