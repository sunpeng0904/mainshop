package com.online.mall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 中奖记录VO
 */
@Data
@Schema(description = "中奖记录")
public class LotteryRecordVO {

    @Schema(description = "记录ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "奖品名称")
    private String prizeName;

    @Schema(description = "奖品等级")
    private Integer prizeLevel;

    @Schema(description = "抽奖时间")
    private LocalDateTime lotteryTime;

    @Schema(description = "领取状态")
    private Integer receiveStatus;

    @Schema(description = "领取时间")
    private LocalDateTime receiveTime;
}
