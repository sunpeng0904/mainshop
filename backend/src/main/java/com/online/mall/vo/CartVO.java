package com.online.mall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购物车视图对象
 */
@Data
@Schema(description = "购物车商品信息")
public class CartVO {
    
    @Schema(description = "购物车ID", example = "1")
    private Long id;
    
    @Schema(description = "用户ID", example = "1")
    private Long userId;
    
    @Schema(description = "商品ID", example = "1")
    private Long productId;
    
    @Schema(description = "商品名称", example = "iPhone 15 Pro")
    private String productName;
    
    @Schema(description = "商品图片")
    private String productImage;
    
    @Schema(description = "商品价格", example = "8999.00")
    private BigDecimal productPrice;
    
    @Schema(description = "商品库存", example = "100")
    private Integer productStock;
    
    @Schema(description = "商品状态 0-下架 1-上架", example = "1")
    private Integer productStatus;
    
    @Schema(description = "数量", example = "2")
    private Integer quantity;
    
    @Schema(description = "是否选中 0-否 1-是", example = "1")
    private Integer selected;
    
    @Schema(description = "小计金额", example = "17998.00")
    private BigDecimal subtotal;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}