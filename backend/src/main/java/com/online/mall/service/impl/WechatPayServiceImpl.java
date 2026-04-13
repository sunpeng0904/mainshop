package com.online.mall.service.impl;

import com.online.mall.config.WechatPayConfig;
import com.online.mall.service.WechatPayService;
import com.online.mall.vo.WechatPayVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 微信支付服务实现
 *
 * 沙箱模式：模拟微信支付API行为
 * 生产模式：调用真实的微信支付API
 */
@Slf4j
@Service
public class WechatPayServiceImpl implements WechatPayService {

    @Autowired
    private WechatPayConfig wechatPayConfig;

    /**
     * 模拟支付状态存储（沙箱模式使用）
     * key: orderNo, value: tradeState
     */
    private final Map<String, String> mockPaymentStatus = new ConcurrentHashMap<>();

    @Override
    public WechatPayVO createNativePayment(Long orderId, String orderNo, String amount, String description) {
        log.info("创建微信Native支付订单: orderId={}, orderNo={}, amount={}", orderId, orderNo, amount);

        WechatPayVO vo = new WechatPayVO();
        vo.setOrderNo(orderNo);
        vo.setAmount(amount);
        vo.setDescription(description);

        if (wechatPayConfig.isSandbox()) {
            // 沙箱模式：生成模拟的支付链接
            String prepayId = "wx" + System.currentTimeMillis();
            String codeUrl = generateMockCodeUrl(orderNo, prepayId);

            vo.setPrepayId(prepayId);
            vo.setCodeUrl(codeUrl);
            vo.setTradeState("NOTPAY");

            // 初始化支付状态为未支付
            mockPaymentStatus.put(orderNo, "NOTPAY");

            log.info("沙箱模式：生成模拟支付链接: {}", codeUrl);
        } else {
            // 生产模式：调用真实微信支付API
            // TODO: 接入真实微信支付
            throw new RuntimeException("请在application.yml配置微信支付商户信息");
        }

        return vo;
    }

    @Override
    public boolean handleCallback(String notifyData) {
        log.info("处理微信支付回调: {}", notifyData);

        if (wechatPayConfig.isSandbox()) {
            // 沙箱模式：模拟回调处理
            // 从模拟状态中更新支付成功
            log.info("沙箱模式：模拟回调处理成功");
            return true;
        }

        // 生产模式：验签并处理回调
        // TODO: 验证签名并解析回调数据
        return false;
    }

    @Override
    public String queryPayStatus(String orderNo) {
        log.info("查询微信支付状态: orderNo={}", orderNo);

        if (wechatPayConfig.isSandbox()) {
            // 沙箱模式：返回模拟状态
            String status = mockPaymentStatus.getOrDefault(orderNo, "NOTPAY");
            log.info("沙箱模式：支付状态={}", status);
            return status;
        }

        // 生产模式：调用真实查询API
        // TODO: 调用微信支付查询接口
        return "NOTPAY";
    }

    @Override
    public void closeOrder(String orderNo) {
        log.info("关闭微信支付订单: orderNo={}", orderNo);

        if (wechatPayConfig.isSandbox()) {
            // 沙箱模式：更新模拟状态
            mockPaymentStatus.put(orderNo, "CLOSED");
            log.info("沙箱模式：订单已关闭");
        } else {
            // 生产模式：调用真实关闭订单API
            // TODO: 调用微信支付关闭订单接口
        }
    }

    /**
     * 沙箱模式：模拟支付成功
     * 前端确认支付后调用此方法
     */
    public void mockPaySuccess(String orderNo) {
        log.info("沙箱模式：模拟支付成功 orderNo={}", orderNo);
        mockPaymentStatus.put(orderNo, "SUCCESS");
    }

    /**
     * 生成模拟的支付二维码链接
     */
    private String generateMockCodeUrl(String orderNo, String prepayId) {
        // 生成一个模拟的weixin://支付链接
        // 实际微信支付会返回类似: weixin://wxpay/bizpayurl?pr=xxx
        return "weixin://wxpay/bizpayurl?pr=" + prepayId + "&order=" + orderNo;
    }

    /**
     * 生成商户订单号
     */
    public static String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        String random = String.format("%04d", (int) (Math.random() * 10000));
        return "WX" + timestamp + random;
    }
}
