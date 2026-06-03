package com.online.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 订单创建DTO
 */
@Data
@Schema(description = "订单创建请求参数")
public class OrderCreateDTO {

    @NotEmpty(message = "{javax.validation.constraints.NotEmpty.message}")
    @Schema(description = "购物车商品ID列表", required = true)
    private List<Long> cartIds;

    @NotBlank(message = "{javax.validation.constraints.NotBlank.message}")
    @Schema(description = "收货地址ID", required = true, example = "c20cb8b7988cc783d5a0a18b74ad3d9a")
    private String addressId;

    @Schema(description = "订单备注")
    private String remark;

    @NotNull(message = "{javax.validation.constraints.NotNull.message}")
    @Schema(description = "支付方式 1-支付宝 2-微信", required = true, example = "1")
    private Integer payType;

    @Schema(description = "优惠券ID")
    private Long couponId;

    @NotBlank(message = "{javax.validation.constraints.NotBlank.message}")
    @Schema(description = "收货人姓名", required = true, example = "张三")
    private String receiverName;

    @NotBlank(message = "{javax.validation.constraints.NotBlank.message}")
    @Schema(description = "收货人电话", required = true, example = "13800138000")
    private String receiverPhone;

    @NotBlank(message = "{javax.validation.constraints.NotBlank.message}")
    @Schema(description = "收货地址", required = true, example = "北京市朝阳区")
    private String receiverAddress;
}
