package com.online.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品查询DTO
 */
@Data
@Schema(description = "商品查询参数")
public class ProductQueryDTO {
    
    @Schema(description = "商品名称")
    private String name;
    
    @Schema(description = "分类ID")
    private Long categoryId;
    
    @Schema(description = "最小价格")
    private BigDecimal minPrice;
    
    @Schema(description = "最大价格")
    private BigDecimal maxPrice;
    
    @Schema(description = "状态 0-下架 1-上架")
    private Integer status;
    
    @Schema(description = "排序字段: price/sales/create_time")
    private String sortField = "create_time";
    
    @Schema(description = "排序方式: asc/desc")
    private String sortOrder = "desc";
    
    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;
    
    @Schema(description = "每页大小", example = "10")
    private Integer pageSize = 10;
}