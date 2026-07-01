package com.mall.payment.controller;

import com.mall.payment.dto.Result;
import com.mall.payment.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 支付服务控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    /**
     * 创建支付
     */
    @PostMapping
    public Result<Map<String, Object>> createPayment(@RequestParam String orderNo,
                                                      @RequestParam(required = false, defaultValue = "WECHAT") String payMethod,
                                                      HttpServletRequest httpRequest) {
        log.info("收到创建支付请求: orderNo={}", orderNo);
        return paymentService.createPayment(orderNo, payMethod, httpRequest);
    }

    /**
     * 支付回调（模拟支付成功）
     */
    @PostMapping("/callback")
    public Result<Void> payCallback(@RequestParam String paymentNo) {
        log.info("收到支付回调: paymentNo={}", paymentNo);
        return paymentService.payCallback(paymentNo);
    }

    /**
     * 退款处理
     */
    @PostMapping("/{id}/refund")
    public Result<Void> refund(@PathVariable("id") Long paymentId,
                                HttpServletRequest httpRequest) {
        log.info("收到退款请求: paymentId={}", paymentId);
        return paymentService.refund(paymentId, httpRequest);
    }
}
