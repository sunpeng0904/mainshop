package com.online.mall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付结果视图对象
 */
@Data
@Schema(description = "支付结果信息")
public class PaymentVO {

    @Schema(description = "支付记录ID")
    private Long id;

    @Schema(description = "支付流水号")
    private String paymentNo;

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "支付金额")
    private BigDecimal amount;

    @Schema(description = "支付方式 1-支付宝 2-微信")
    private Integer payType;

    @Schema(description = "支付方式名称")
    private String payTypeName;

    @Schema(description = "支付状态 0-待支付 1-支付成功 2-支付失败")
    private Integer status;

    @Schema(description = "支付状态名称")
    private String statusName;

    @Schema(description = "支付时间")
    private LocalDateTime payTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    // 用于返回给前端的支付参数
    @Schema(description = "支付参数（用于唤起支付）")
    private String payParams;
}
