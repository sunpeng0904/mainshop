package com.online.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 支付请求DTO
 */
@Data
@Schema(description = "支付请求参数")
public class PaymentDTO {

    @NotNull(message = "{javax.validation.constraints.NotNull.message}")
    @Schema(description = "订单ID", required = true, example = "1")
    private Long orderId;

    @NotNull(message = "{javax.validation.constraints.NotNull.message}")
    @Schema(description = "支付方式 1-支付宝 2-微信", required = true, example = "1")
    private Integer payType;
}
