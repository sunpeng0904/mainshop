package com.online.mall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单视图对象
 */
@Data
@Schema(description = "订单信息")
public class OrderVO {
    
    @Schema(description = "订单ID", example = "1")
    private Long id;
    
    @Schema(description = "订单编号", example = "20250407123456789")
    private String orderNo;
    
    @Schema(description = "用户ID", example = "1")
    private Long userId;
    
    @Schema(description = "用户名", example = "zhangsan")
    private String username;
    
    @Schema(description = "订单总金额", example = "8999.00")
    private BigDecimal totalAmount;
    
    @Schema(description = "实付金额", example = "8999.00")
    private BigDecimal payAmount;
    
    @Schema(description = "运费金额", example = "0.00")
    private BigDecimal freightAmount;
    
    @Schema(description = "优惠金额", example = "0.00")
    private BigDecimal discountAmount;
    
    @Schema(description = "支付方式 1-支付宝 2-微信", example = "1")
    private Integer payType;
    
    @Schema(description = "支付方式名称", example = "支付宝")
    private String payTypeName;
    
    @Schema(description = "订单状态 0-待付款 1-待发货 2-已发货 3-已完成 4-已取消", example = "0")
    private Integer status;
    
    @Schema(description = "订单状态名称", example = "待付款")
    private String statusName;
    
    @Schema(description = "收货人姓名", example = "张三")
    private String receiverName;
    
    @Schema(description = "收货人电话", example = "13800138000")
    private String receiverPhone;
    
    @Schema(description = "收货地址", example = "北京市朝阳区")
    private String receiverAddress;
    
    @Schema(description = "订单备注")
    private String remark;
    
    @Schema(description = "支付时间")
    private LocalDateTime payTime;
    
    @Schema(description = "发货时间")
    private LocalDateTime deliveryTime;
    
    @Schema(description = "收货时间")
    private LocalDateTime receiveTime;
    
    @Schema(description = "取消时间")
    private LocalDateTime cancelTime;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    
    @Schema(description = "订单商品列表")
    private List<OrderItemVO> items;
}