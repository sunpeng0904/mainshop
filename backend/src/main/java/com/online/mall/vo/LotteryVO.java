package com.online.mall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 抽奖结果VO
 */
@Data
@Schema(description = "抽奖结果")
public class LotteryVO {

    @Schema(description = "是否中奖")
    private Boolean win;

    @Schema(description = "奖品ID")
    private Long prizeId;

    @Schema(description = "奖品名称")
    private String prizeName;

    @Schema(description = "奖品等级 1-一等奖 2-二等奖 3-三等奖 4-谢谢参与")
    private Integer prizeLevel;

    @Schema(description = "奖品图片")
    private String prizeImage;

    @Schema(description = "奖品价值")
    private BigDecimal prizeValue;

    @Schema(description = "剩余抽奖次数")
    private Integer remainingTimes;

    @Schema(description = "转盘旋转角度")
    private Integer rotateAngle;
}
