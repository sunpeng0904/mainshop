package com.online.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付记录实体
 */
@Data
@TableName("payment")
public class Payment {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 支付流水号
     */
    private String paymentNo;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 支付金额
     */
    private BigDecimal amount;

    /**
     * 支付方式 1-支付宝 2-微信
     */
    private Integer payType;

    /**
     * 支付状态 0-待支付 1-支付成功 2-支付失败 3-已退款
     */
    private Integer status;

    /**
     * 第三方交易号
     */
    private String tradeNo;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 退款时间
     */
    private LocalDateTime refundTime;

    /**
     * 退款金额
     */
    private BigDecimal refundAmount;

    /**
     * 支付通知数据
     */
    private String notifyData;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
