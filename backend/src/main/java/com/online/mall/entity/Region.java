package com.online.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 区域实体（符合ATTRC2E词根规范）
 */
@Data
@TableName("region_tb")
public class Region {

    /**
     * 主键标识
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 区域编码
     */
    private String cde;

    /**
     * 区域名称
     */
    private String name;

    /**
     * 父级编码
     */
    private String prntCde;

    /**
     * 级别 1-省 2-市 3-区
     */
    private Integer lvl;

    /**
     * 创建人标识
     */
    @TableField(fill = FieldFill.INSERT)
    private String entrPsnId;

    /**
     * 创建人姓名
     */
    @TableField(fill = FieldFill.INSERT)
    private String entrPsnName;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime entrTime;

    /**
     * 最后修改人标识
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String lastAlterPsnId;

    /**
     * 最后修改人姓名
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String lastAlterPsnName;

    /**
     * 最后修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime lastAlterTime;

    /**
     * 删除标志 Y-是 N-否
     */
    @TableLogic
    private String vldStsCde;
}
