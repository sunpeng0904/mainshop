package com.online.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.online.mall.common.BusinessException;
import com.online.mall.dto.PaymentDTO;
import com.online.mall.entity.Order;
import com.online.mall.entity.Payment;
import com.online.mall.mapper.PaymentMapper;
import com.online.mall.service.OrderService;
import com.online.mall.service.PaymentService;
import com.online.mall.service.WechatPayService;
import com.online.mall.vo.PaymentVO;
import com.online.mall.vo.WechatPayVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 支付服务实现
 */
@Slf4j
@Service
public class PaymentServiceImpl extends ServiceImpl<PaymentMapper, Payment> implements PaymentService {

    @Autowired
    private OrderService orderService;

    @Autowired
    private WechatPayService wechatPayService;

    @Override
    @Transactional
    public PaymentVO createPayment(Long userId, PaymentDTO paymentDTO) {
        log.info("创建支付: userId={}, orderId={}", userId, paymentDTO.getOrderId());

        // 获取订单
        Order order = orderService.getById(paymentDTO.getOrderId());
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }

        if (order.getStatus() != Order.STATUS_PENDING_PAYMENT) {
            throw new BusinessException("订单状态不允许支付");
        }

        // 检查是否已存在支付记录
        Payment existPayment = getOne(new QueryWrapper<Payment>()
                .eq("order_id", order.getId())
                .eq("status", PaymentStatus.PENDING));
        if (existPayment != null) {
            return convertToVO(existPayment);
        }

        // 创建支付记录
        Payment payment = new Payment();
        payment.setPaymentNo(generatePaymentNo());
        payment.setOrderId(order.getId());
        payment.setOrderNo(order.getOrderNo());
        payment.setUserId(userId);
        payment.setAmount(order.getPayAmount());
        payment.setPayType(paymentDTO.getPayType());
        payment.setStatus(PaymentStatus.PENDING);

        save(payment);

        log.info("支付记录创建成功: paymentNo={}", payment.getPaymentNo());

        PaymentVO vo = convertToVO(payment);
        // 模拟支付参数（实际项目中应该调用第三方支付API）
        vo.setPayParams("mock_pay_params_" + payment.getPaymentNo());

        return vo;
    }

    @Override
    @Transactional
    public PaymentVO mockPaymentSuccess(Long userId, String paymentNo) {
        log.info("模拟支付成功: userId={}, paymentNo={}", userId, paymentNo);

        Payment payment = getOne(new QueryWrapper<Payment>().eq("payment_no", paymentNo));
        if (payment == null || !payment.getUserId().equals(userId)) {
            throw new BusinessException("支付记录不存在");
        }

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new BusinessException("支付状态不正确");
        }

        // 更新支付状态
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setTradeNo("TRADE_" + System.currentTimeMillis());
        payment.setPayTime(LocalDateTime.now());
        payment.setNotifyData("{\"status\":\"success\",\"mock\":true}");
        updateById(payment);

        // 更新订单状态
        Order order = orderService.getById(payment.getOrderId());
        if (order != null && order.getStatus() == Order.STATUS_PENDING_PAYMENT) {
            order.setStatus(Order.STATUS_PENDING_SHIPMENT);
            order.setPayTime(LocalDateTime.now());
            order.setPayType(payment.getPayType());
            orderService.updateById(order);
        }

        log.info("支付成功: paymentNo={}, orderNo={}", paymentNo, payment.getOrderNo());

        return convertToVO(payment);
    }

    @Override
    @Transactional
    public void handlePaymentCallback(String paymentNo, String tradeNo, String notifyData) {
        log.info("处理支付回调: paymentNo={}, tradeNo={}", paymentNo, tradeNo);

        Payment payment = getOne(new QueryWrapper<Payment>().eq("payment_no", paymentNo));
        if (payment == null) {
            log.error("支付记录不存在: paymentNo={}", paymentNo);
            return;
        }

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            log.info("支付已处理: paymentNo={}", paymentNo);
            return;
        }

        // 更新支付状态
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setTradeNo(tradeNo);
        payment.setPayTime(LocalDateTime.now());
        payment.setNotifyData(notifyData);
        updateById(payment);

        // 更新订单状态
        Order order = orderService.getById(payment.getOrderId());
        if (order != null && order.getStatus() == Order.STATUS_PENDING_PAYMENT) {
            order.setStatus(Order.STATUS_PENDING_SHIPMENT);
            order.setPayTime(LocalDateTime.now());
            order.setPayType(payment.getPayType());
            orderService.updateById(order);
        }
    }

    @Override
    @Transactional
    public void refund(Long userId, Long orderId, String reason) {
        log.info("申请退款: userId={}, orderId={}, reason={}", userId, orderId, reason);

        Order order = orderService.getById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }

        // 检查订单状态
        if (order.getStatus() != Order.STATUS_PENDING_SHIPMENT && order.getStatus() != Order.STATUS_SHIPPED) {
            throw new BusinessException("当前订单状态不允许退款");
        }

        // 获取支付记录
        Payment payment = getOne(new QueryWrapper<Payment>()
                .eq("order_id", orderId)
                .eq("status", PaymentStatus.SUCCESS));

        if (payment == null) {
            throw new BusinessException("支付记录不存在");
        }

        // 更新支付记录
        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setRefundAmount(payment.getAmount());
        payment.setRefundTime(LocalDateTime.now());
        updateById(payment);

        // 更新订单状态
        order.setStatus(Order.STATUS_REFUNDED);
        order.setCancelReason(reason);
        order.setCancelTime(LocalDateTime.now());
        orderService.updateById(order);

        log.info("退款成功: orderId={}", orderId);
    }

    @Override
    public PaymentVO getPaymentByOrderId(Long userId, Long orderId) {
        Payment payment = getOne(new QueryWrapper<Payment>()
                .eq("order_id", orderId)
                .orderByDesc("create_time")
                .last("LIMIT 1"));

        if (payment == null || !payment.getUserId().equals(userId)) {
            return null;
        }

        return convertToVO(payment);
    }

    @Override
    public PaymentVO getPaymentByPaymentNo(Long userId, String paymentNo) {
        Payment payment = getOne(new QueryWrapper<Payment>().eq("payment_no", paymentNo));

        if (payment == null || !payment.getUserId().equals(userId)) {
            return null;
        }

        return convertToVO(payment);
    }

    @Override
    @Transactional
    public WechatPayVO createWechatNativePayment(Long userId, Long orderId) {
        log.info("创建微信Native支付: userId={}, orderId={}", userId, orderId);

        // 获取订单
        Order order = orderService.getById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }

        if (order.getStatus() != Order.STATUS_PENDING_PAYMENT) {
            throw new BusinessException("订单状态不允许支付");
        }

        // 创建支付记录
        Payment payment = getOne(new QueryWrapper<Payment>()
                .eq("order_id", order.getId())
                .eq("status", PaymentStatus.PENDING));

        if (payment == null) {
            payment = new Payment();
            payment.setPaymentNo(generatePaymentNo());
            payment.setOrderId(order.getId());
            payment.setOrderNo(order.getOrderNo());
            payment.setUserId(userId);
            payment.setAmount(order.getPayAmount());
            payment.setPayType(Order.PAY_TYPE_WECHAT);
            payment.setStatus(PaymentStatus.PENDING);
            save(payment);
        }

        // 调用微信支付服务
        WechatPayVO vo = wechatPayService.createNativePayment(
                orderId,
                order.getOrderNo(),
                order.getPayAmount().toString(),
                "订单-" + order.getOrderNo()
        );

        log.info("微信Native支付创建成功: orderNo={}, codeUrl={}", order.getOrderNo(), vo.getCodeUrl());
        return vo;
    }

    @Override
    @Transactional
    public boolean handleWechatCallback(String notifyData) {
        log.info("处理微信支付回调");

        // 沙箱模式：模拟处理
        // 实际生产环境需要验签并解析回调数据
        boolean success = wechatPayService.handleCallback(notifyData);

        if (success) {
            log.info("微信支付回调处理成功");
        }

        return success;
    }

    @Override
    @Transactional
    public void mockWechatPaySuccess(String orderNo) {
        log.info("模拟微信支付成功: orderNo={}", orderNo);

        // 获取订单
        Order order = orderService.getOne(new QueryWrapper<Order>().eq("order_no", orderNo));
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 获取支付记录
        Payment payment = getOne(new QueryWrapper<Payment>()
                .eq("order_id", order.getId())
                .eq("status", PaymentStatus.PENDING));

        if (payment == null) {
            throw new BusinessException("支付记录不存在");
        }

        // 更新支付状态
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setTradeNo("WX_TRADE_" + System.currentTimeMillis());
        payment.setPayTime(LocalDateTime.now());
        payment.setNotifyData("{\"status\":\"success\",\"mock\":true,\"payType\":\"wechat\"}");
        updateById(payment);

        // 更新订单状态
        if (order.getStatus() == Order.STATUS_PENDING_PAYMENT) {
            order.setStatus(Order.STATUS_PENDING_SHIPMENT);
            order.setPayTime(LocalDateTime.now());
            order.setPayType(Order.PAY_TYPE_WECHAT);
            orderService.updateById(order);
        }

        log.info("微信支付成功: orderNo={}", orderNo);
    }

    /**
     * 生成支付流水号
     */
    private String generatePaymentNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        String random = String.format("%04d", (int) (Math.random() * 10000));
        return "PAY" + timestamp + random;
    }

    /**
     * 转换为VO
     */
    private PaymentVO convertToVO(Payment payment) {
        PaymentVO vo = new PaymentVO();
        BeanUtils.copyProperties(payment, vo);

        // 支付方式名称
        if (payment.getPayType() != null) {
            vo.setPayTypeName(payment.getPayType() == 1 ? "支付宝" : "微信");
        }

        // 状态名称
        vo.setStatusName(getStatusName(payment.getStatus()));

        return vo;
    }

    /**
     * 获取状态名称
     */
    private String getStatusName(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "待支付";
            case 1: return "支付成功";
            case 2: return "支付失败";
            case 3: return "已退款";
            default: return "未知";
        }
    }

    /**
     * 支付状态常量
     */
    public static class PaymentStatus {
        public static final int PENDING = 0;   // 待支付
        public static final int SUCCESS = 1;   // 支付成功
        public static final int FAILED = 2;    // 支付失败
        public static final int REFUNDED = 3;  // 已退款
    }
}
