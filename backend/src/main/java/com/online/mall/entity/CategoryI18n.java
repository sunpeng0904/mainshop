package com.online.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分类多语言信息实体
 */
@Data
@TableName("category_i18n")
public class CategoryI18n {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 语言代码（zh_CN, en_US）
     */
    private String locale;

    /**
     * 分类名称（多语言）
     */
    private String name;

    /**
     * 分类描述（多语言）
     */
    private String description;

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
