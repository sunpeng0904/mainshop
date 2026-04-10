package com.online.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体
 */
@Data
@TableName("t_order")
public class Order {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 实付金额
     */
    private BigDecimal payAmount;

    /**
     * 运费金额
     */
    private BigDecimal freightAmount;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 支付方式 1-支付宝 2-微信
     */
    private Integer payType;

    /**
     * 订单状态 0-待付款 1-待发货 2-已发货 3-已完成 4-已取消 5-已退款
     */
    private Integer status;

    /**
     * 收货人姓名
     */
    private String receiverName;

    /**
     * 收货人电话
     */
    private String receiverPhone;

    /**
     * 收货地址
     */
    private String receiverAddress;

    /**
     * 物流公司
     */
    private String shippingCompany;

    /**
     * 物流单号
     */
    private String shippingNumber;

    /**
     * 订单备注
     */
    private String remark;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 发货时间
     */
    private LocalDateTime deliveryTime;

    /**
     * 收货时间
     */
    private LocalDateTime receiveTime;

    /**
     * 取消时间
     */
    private LocalDateTime cancelTime;

    /**
     * 取消原因
     */
    private String cancelReason;

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

    /**
     * 删除标志
     */
    @TableLogic
    private Integer deleted;

    // ========== 订单状态常量 ==========
    public static final int STATUS_PENDING_PAYMENT = 0;   // 待付款
    public static final int STATUS_PENDING_SHIPMENT = 1;  // 待发货
    public static final int STATUS_SHIPPED = 2;           // 已发货
    public static final int STATUS_COMPLETED = 3;         // 已完成
    public static final int STATUS_CANCELLED = 4;        // 已取消
    public static final int STATUS_REFUNDED = 5;          // 已退款

    // ========== 支付方式常量 ==========
    public static final int PAY_TYPE_ALIPAY = 1;  // 支付宝
    public static final int PAY_TYPE_WECHAT = 2;  // 微信
}
