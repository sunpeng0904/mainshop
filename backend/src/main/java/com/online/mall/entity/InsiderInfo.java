package com.online.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 内幕信息知情人登记实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_insider_info")
public class InsiderInfo {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 企业名称
     */
    private String companyName;

    /**
     * 受理时间
     */
    private LocalDate acceptTime;

    /**
     * 所属板块: 主板（沪市）, 主板（深市）, 科创板, 创业板
     */
    private String board;

    /**
     * 融资类型: 首次公开发行股票, 向不特定对象募集股份, 向特定对象发行股票
     */
    private String financingType;

    /**
     * 证监会行业细分
     */
    private String industry;

    /**
     * 知情日期
     */
    private LocalDate knowledgeTime;

    /**
     * 理由
     */
    private String reason;

    /**
     * 知情内容
     */
    private String content;

    /**
     * 知情人姓名
     */
    private String insiderName;

    /**
     * 登记时间
     */
    private LocalDate registerTime;

    /**
     * 状态: draft-草稿, approved-审核通过, rejected-审核不通过, pending-待审核, submitted-已提交, approved_submitted-审核通过（已提交）
     */
    private String status;

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
