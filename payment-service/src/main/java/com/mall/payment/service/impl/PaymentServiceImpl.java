package com.mall.payment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.payment.client.OrderClient;
import com.mall.payment.dto.Result;
import com.mall.payment.entity.Payment;
import com.mall.payment.mapper.PaymentMapper;
import com.mall.payment.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 支付服务实现
 */
@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired(required = false)
    private OrderClient orderClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Map<String, Object>> createPayment(String orderNo, String payMethod, HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);

        // 检查是否已有支付记录
        LambdaQueryWrapper<Payment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Payment::getOrderNo, orderNo);
        Payment existingPayment = paymentMapper.selectOne(wrapper);
        if (existingPayment != null) {
            if ("PAID".equals(existingPayment.getStatus())) {
                return Result.fail(400, "该订单已支付");
            }
            // 返回已有支付记录
            Map<String, Object> data = new HashMap<>();
            data.put("paymentId", existingPayment.getId());
            data.put("paymentNo", existingPayment.getPaymentNo());
            data.put("status", existingPayment.getStatus());
            return Result.success(data);
        }

        // 从订单服务获取订单金额
        BigDecimal amount = BigDecimal.ZERO;
        if (orderClient != null) {
            try {
                Result<Map<String, Object>> orderResult = orderClient.getOrderByNo(orderNo);
                if (orderResult.isSuccess() && orderResult.getData() != null) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> orderMap = (Map<String, Object>) orderResult.getData().get("order");
                    if (orderMap != null && orderMap.get("totalAmount") != null) {
                        amount = new BigDecimal(orderMap.get("totalAmount").toString());
                    }
                }
            } catch (Exception e) {
                log.warn("获取订单金额失败，使用默认金额0: {}", e.getMessage());
            }
        }

        // 生成支付流水号
        String paymentNo = "PAY" + System.currentTimeMillis() + String.format("%04d", new Random().nextInt(10000));

        Payment payment = new Payment();
        payment.setPaymentNo(paymentNo);
        payment.setOrderNo(orderNo);
        payment.setUserId(userId);
        payment.setAmount(amount);
        payment.setStatus("UNPAID");
        payment.setPayMethod(payMethod != null ? payMethod : "WECHAT");
        payment.setCreateTime(LocalDateTime.now());
        payment.setUpdateTime(LocalDateTime.now());
        paymentMapper.insert(payment);

        log.info("创建支付记录: paymentNo={}, orderNo={}, userId={}, amount={}", paymentNo, orderNo, userId, amount);

        Map<String, Object> data = new HashMap<>();
        data.put("paymentId", payment.getId());
        data.put("paymentNo", paymentNo);
        data.put("status", "UNPAID");
        data.put("amount", amount);
        return Result.success(data);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> payCallback(String paymentNo) {
        LambdaQueryWrapper<Payment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Payment::getPaymentNo, paymentNo);
        Payment payment = paymentMapper.selectOne(wrapper);

        if (payment == null) {
            return Result.fail(404, "支付记录不存在");
        }
        if (!"UNPAID".equals(payment.getStatus())) {
            return Result.fail(400, "支付状态异常，当前状态: " + payment.getStatus());
        }

        // 模拟支付成功
        payment.setStatus("PAID");
        payment.setPayTime(LocalDateTime.now());
        payment.setUpdateTime(LocalDateTime.now());
        paymentMapper.updateById(payment);

        log.info("支付成功: paymentNo={}, orderNo={}, amount={}", paymentNo, payment.getOrderNo(), payment.getAmount());
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> refund(Long paymentId, HttpServletRequest httpRequest) {
        Payment payment = paymentMapper.selectById(paymentId);
        if (payment == null) {
            return Result.fail(404, "支付记录不存在");
        }
        if (!"PAID".equals(payment.getStatus())) {
            return Result.fail(400, "只有已支付订单才能退款");
        }

        payment.setStatus("REFUNDED");
        payment.setRefundTime(LocalDateTime.now());
        payment.setUpdateTime(LocalDateTime.now());
        paymentMapper.updateById(payment);

        log.info("退款成功: paymentNo={}, paymentId={}", payment.getPaymentNo(), paymentId);
        return Result.success();
    }

    private Long getUserIdFromRequest(HttpServletRequest request) {
        String userIdStr = request.getHeader("X-User-Id");
        if (userIdStr == null) {
            throw new RuntimeException("未获取到用户信息");
        }
        return Long.valueOf(userIdStr);
    }
}
