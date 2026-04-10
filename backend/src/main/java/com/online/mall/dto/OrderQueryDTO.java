package com.online.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 订单查询DTO
 */
@Data
@Schema(description = "订单查询参数")
public class OrderQueryDTO {

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "订单状态 0-待付款 1-待发货 2-已发货 3-已完成 4-已取消")
    private Integer status;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer pageSize = 10;
}
