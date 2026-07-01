package com.mall.payment.service;

import com.mall.payment.dto.Result;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 支付服务接口
 */
public interface PaymentService {

    /**
     * 创建支付
     */
    Result<Map<String, Object>> createPayment(String orderNo, String payMethod, HttpServletRequest httpRequest);

    /**
     * 支付回调（模拟支付成功）
     */
    Result<Void> payCallback(String paymentNo);

    /**
     * 退款处理
     */
    Result<Void> refund(Long paymentId, HttpServletRequest httpRequest);
}
