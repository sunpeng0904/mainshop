package com.online.mall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品视图对象
 */
@Data
@Schema(description = "商品信息")
public class ProductVO {
    
    @Schema(description = "商品ID", example = "1")
    private Long id;
    
    @Schema(description = "商品名称", example = "iPhone 15 Pro")
    private String name;
    
    @Schema(description = "分类ID", example = "1")
    private Long categoryId;
    
    @Schema(description = "分类名称", example = "手机")
    private String categoryName;
    
    @Schema(description = "价格", example = "8999.00")
    private BigDecimal price;
    
    @Schema(description = "原价", example = "9999.00")
    private BigDecimal originalPrice;
    
    @Schema(description = "库存", example = "100")
    private Integer stock;
    
    @Schema(description = "销量", example = "50")
    private Integer sales;
    
    @Schema(description = "商品图片列表")
    private List<String> images;
    
    @Schema(description = "商品描述")
    private String description;
    
    @Schema(description = "商品详情")
    private String detail;
    
    @Schema(description = "规格参数")
    private Object specifications;
    
    @Schema(description = "状态 0-下架 1-上架", example = "1")
    private Integer status;
    
    @Schema(description = "是否收藏")
    private Boolean isFavorite = false;
    
    @Schema(description = "购物车数量（如果已加入购物车）")
    private Integer cartQuantity = 0;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}