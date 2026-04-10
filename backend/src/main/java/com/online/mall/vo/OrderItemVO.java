package com.online.mall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单商品项视图对象
 */
@Data
@Schema(description = "订单商品项信息")
public class OrderItemVO {

    @Schema(description = "订单项ID", example = "1")
    private Long id;

    @Schema(description = "订单ID", example = "1")
    private Long orderId;

    @Schema(description = "商品ID", example = "1")
    private Long productId;

    @Schema(description = "商品名称", example = "iPhone 15 Pro")
    private String productName;

    @Schema(description = "商品图片")
    private String productImage;

    @Schema(description = "商品价格（下单时）", example = "8999.00")
    private BigDecimal productPrice;

    @Schema(description = "购买数量", example = "2")
    private Integer quantity;

    @Schema(description = "小计金额", example = "17998.00")
    private BigDecimal subtotal;

    @Schema(description = "规格参数")
    private Object specifications;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}