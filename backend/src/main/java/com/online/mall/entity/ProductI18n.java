package com.online.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品多语言信息实体
 */
@Data
@TableName("product_i18n")
public class ProductI18n {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 语言代码（zh_CN, en_US）
     */
    private String locale;

    /**
     * 商品名称（多语言）
     */
    private String name;

    /**
     * 商品描述（多语言）
     */
    private String description;

    /**
     * 商品详情（多语言）
     */
    private String detail;

    /**
     * 规格参数（多语言）
     */
    private String specifications;

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
