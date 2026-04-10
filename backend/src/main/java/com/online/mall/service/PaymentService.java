package com.online.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.online.mall.dto.PaymentDTO;
import com.online.mall.entity.Payment;
import com.online.mall.vo.PaymentVO;

/**
 * 支付服务接口
 */
public interface PaymentService extends IService<Payment> {

    /**
     * 创建支付
     */
    PaymentVO createPayment(Long userId, PaymentDTO paymentDTO);

    /**
     * 模拟支付成功
     */
    PaymentVO mockPaymentSuccess(Long userId, String paymentNo);

    /**
     * 处理支付回调
     */
    void handlePaymentCallback(String paymentNo, String tradeNo, String notifyData);

    /**
     * 申请退款
     */
    void refund(Long userId, Long orderId, String reason);

    /**
     * 根据订单ID获取支付记录
     */
    PaymentVO getPaymentByOrderId(Long userId, Long orderId);

    /**
     * 根据支付流水号获取支付记录
     */
    PaymentVO getPaymentByPaymentNo(Long userId, String paymentNo);
}
