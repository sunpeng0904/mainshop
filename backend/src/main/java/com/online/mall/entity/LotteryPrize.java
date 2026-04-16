package com.online.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 抽奖奖品实体
 */
@Data
@TableName("lottery_prize")
public class LotteryPrize {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 奖品名称
     */
    private String name;

    /**
     * 奖品等级 1-一等奖 2-二等奖 3-三等奖 4-谢谢参与
     */
    private Integer level;

    /**
     * 奖品图片
     */
    private String image;

    /**
     * 奖品价值
     */
    private BigDecimal value;

    /**
     * 中奖概率（百分比，如0.1表示0.1%）
     */
    private BigDecimal probability;

    /**
     * 库存数量
     */
    private Integer stock;

    /**
     * 已发放数量
     */
    private Integer issued;

    /**
     * 状态 0-禁用 1-启用
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 乐观锁版本号
     */
    @Version
    private Integer version;

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
