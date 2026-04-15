package com.online.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 抽奖记录实体
 */
@Data
@TableName("lottery_record")
public class LotteryRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 奖品ID
     */
    private Long prizeId;

    /**
     * 奖品名称
     */
    private String prizeName;

    /**
     * 奖品等级
     */
    private Integer prizeLevel;

    /**
     * 抽奖时间
     */
    private LocalDateTime lotteryTime;

    /**
     * 领取状态 0-未领取 1-已领取
     */
    private Integer receiveStatus;

    /**
     * 领取时间
     */
    private LocalDateTime receiveTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
