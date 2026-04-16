package com.online.mall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 地址视图对象
 */
@Data
@Schema(description = "地址信息")
public class AddressVO {

    @Schema(description = "地址ID")
    private Long id;

    @Schema(description = "收货人姓名")
    private String receiverName;

    @Schema(description = "收货人电话")
    private String receiverPhone;

    @Schema(description = "省份编码")
    private String provinceCode;

    @Schema(description = "省份名称")
    private String provinceName;

    @Schema(description = "城市编码")
    private String cityCode;

    @Schema(description = "城市名称")
    private String cityName;

    @Schema(description = "区县编码")
    private String districtCode;

    @Schema(description = "区县名称")
    private String districtName;

    @Schema(description = "详细地址")
    private String detailAddress;

    @Schema(description = "完整地址")
    private String fullAddress;

    @Schema(description = "是否默认地址 0-否 1-是")
    private Integer isDefault;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
