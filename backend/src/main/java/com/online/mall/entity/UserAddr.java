package com.online.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户地址实体（符合ATTRC2E词根规范）
 */
@Data
@TableName("user_addr_tb")
public class UserAddr {

    /**
     * 主键标识
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 用户标识
     */
    private String userId;

    /**
     * 收货人姓名
     */
    private String rcvrName;

    /**
     * 收货人电话
     */
    private String rcvrTel;

    /**
     * 省份编码
     */
    private String prvcCde;

    /**
     * 城市编码
     */
    private String cityCde;

    /**
     * 区县编码
     */
    private String dstrctCde;

    /**
     * 详细地址
     */
    private String dtlAddr;

    /**
     * 是否默认标志 Y-是 N-否
     */
    private String dftIndc;

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
    @TableLogic(delval = "Y", value = "N")
    private String vldStsCde;
}
