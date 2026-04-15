package com.online.mall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 奖品VO
 */
@Data
@Schema(description = "奖品信息")
public class LotteryPrizeVO {

    @Schema(description = "奖品ID")
    private Long id;

    @Schema(description = "奖品名称")
    private String name;

    @Schema(description = "奖品等级")
    private Integer level;

    @Schema(description = "奖品图片")
    private String image;

    @Schema(description = "奖品价值")
    private BigDecimal value;

    @Schema(description = "中奖概率")
    private BigDecimal probability;

    @Schema(description = "库存")
    private Integer stock;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "排序")
    private Integer sort;
}
