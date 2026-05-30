package com.online.mall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 地址视图对象（符合ATTRC2E词根规范）
 */
@Data
@Schema(description = "地址信息")
public class AddressVO {

    @Schema(description = "地址标识")
    private String id;

    @Schema(description = "收货人姓名")
    private String rcvrName;

    @Schema(description = "收货人电话")
    private String rcvrTel;

    @Schema(description = "省份编码")
    private String prvcCde;

    @Schema(description = "省份名称")
    private String prvcName;

    @Schema(description = "城市编码")
    private String cityCde;

    @Schema(description = "城市名称")
    private String cityName;

    @Schema(description = "区县编码")
    private String dstrctCde;

    @Schema(description = "区县名称")
    private String dstrctName;

    @Schema(description = "详细地址")
    private String dtlAddr;

    @Schema(description = "完整地址")
    private String fullAddr;

    @Schema(description = "是否默认标志 Y-是 N-否")
    private String dftIndc;

    @Schema(description = "创建时间")
    private LocalDateTime entrTime;

    @Schema(description = "最后修改时间")
    private LocalDateTime lastAlterTime;
}
