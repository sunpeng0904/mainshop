package com.online.mall.service;

import com.online.mall.vo.WechatPayVO;

/**
 * 微信支付服务接口
 */
public interface WechatPayService {

    /**
     * 创建Native支付订单（生成二维码链接）
     * @param orderId 订单ID
     * @param orderNo 订单编号
     * @param amount 支付金额（元）
     * @param description 商品描述
     * @return 支付信息（包含二维码链接）
     */
    WechatPayVO createNativePayment(Long orderId, String orderNo, String amount, String description);

    /**
     * 处理支付回调
     * @param notifyData 回调数据
     * @return 是否处理成功
     */
    boolean handleCallback(String notifyData);

    /**
     * 查询支付状态
     * @param orderNo 订单编号
     * @return 支付状态
     */
    String queryPayStatus(String orderNo);

    /**
     * 关闭订单
     * @param orderNo 订单编号
     */
    void closeOrder(String orderNo);
}
