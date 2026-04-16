package com.online.mall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 区域视图对象
 */
@Data
@Schema(description = "区域信息")
public class RegionVO {

    @Schema(description = "区域ID")
    private Long id;

    @Schema(description = "区域编码")
    private String regionCode;

    @Schema(description = "区域名称")
    private String regionName;

    @Schema(description = "父级编码")
    private String parentCode;

    @Schema(description = "层级: 1-省 2-市 3-区")
    private Integer level;

    @Schema(description = "排序")
    private Integer sort;
}
