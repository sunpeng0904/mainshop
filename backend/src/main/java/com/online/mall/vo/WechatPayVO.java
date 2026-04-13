package com.online.mall.vo;

import lombok.Data;

/**
 * 微信支付返回信息
 */
@Data
public class WechatPayVO {

    /**
     * 预支付交易会话标识
     */
    private String prepayId;

    /**
     * 二维码链接（用于生成二维码）
     */
    private String codeUrl;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 支付金额（分）
     */
    private String amount;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 支付状态: NOTPAY-未支付, SUCCESS-支付成功, CLOSED-已关闭
     */
    private String tradeState;

    /**
     * 第三方交易号
     */
    private String transactionId;
}
