package com.online.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 区域实体（省市区）
 */
@Data
@TableName("sys_region")
public class Region {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 区域编码（国标行政区划代码）
     */
    private String regionCode;

    /**
     * 区域名称
     */
    private String regionName;

    /**
     * 父级编码
     */
    private String parentCode;

    /**
     * 层级: 1-省 2-市 3-区
     */
    private Integer level;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态 0-禁用 1-启用
     */
    private Integer status;

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
     * 删除标志 0-未删除 1-已删除
     */
    @TableLogic
    private Integer deleted;
}
