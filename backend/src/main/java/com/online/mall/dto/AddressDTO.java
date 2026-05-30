package com.online.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 地址DTO（符合ATTRC2E词根规范）
 */
@Data
@Schema(description = "地址请求参数")
public class AddressDTO {

    @Schema(description = "地址标识（更新时需要）")
    private String id;

    @NotBlank(message = "收货人姓名不能为空")
    @Size(max = 100, message = "收货人姓名不能超过100字符")
    @Schema(description = "收货人姓名", required = true, example = "张三")
    private String rcvrName;

    @NotBlank(message = "收货人电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "收货人电话", required = true, example = "13800138000")
    private String rcvrTel;

    @NotBlank(message = "省份编码不能为空")
    @Schema(description = "省份编码", required = true)
    private String prvcCde;

    @NotBlank(message = "城市编码不能为空")
    @Schema(description = "城市编码", required = true)
    private String cityCde;

    @NotBlank(message = "区县编码不能为空")
    @Schema(description = "区县编码", required = true)
    private String dstrctCde;

    @NotBlank(message = "详细地址不能为空")
    @Size(max = 500, message = "详细地址不能超过500字符")
    @Schema(description = "详细地址", required = true, example = "望京SOHO T1 1001室")
    private String dtlAddr;

    @Schema(description = "是否默认标志 Y-是 N-否", example = "N")
    private String dftIndc;
}
