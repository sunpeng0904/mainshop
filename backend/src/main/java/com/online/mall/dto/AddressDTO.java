package com.online.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 地址DTO
 */
@Data
@Schema(description = "地址请求参数")
public class AddressDTO {

    @Schema(description = "地址ID（更新时需要）")
    private Long id;

    @NotBlank(message = "{javax.validation.constraints.NotBlank.message}")
    @Schema(description = "收货人姓名", required = true, example = "张三")
    private String receiverName;

    @NotBlank(message = "{javax.validation.constraints.NotBlank.message}")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "{javax.validation.constraints.Pattern.message}")
    @Schema(description = "收货人电话", required = true, example = "13800138000")
    private String receiverPhone;

    @Schema(description = "省份编码")
    private String provinceCode;

    @Schema(description = "城市编码")
    private String cityCode;

    @Schema(description = "区县编码")
    private String districtCode;

    @NotBlank(message = "{javax.validation.constraints.NotBlank.message}")
    @Schema(description = "详细地址", required = true, example = "望京SOHO T1 1001室")
    private String detailAddress;

    @Schema(description = "是否默认地址 0-否 1-是", example = "0")
    private Integer isDefault;
}
