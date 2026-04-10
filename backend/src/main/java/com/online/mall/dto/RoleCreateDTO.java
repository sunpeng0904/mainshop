package com.online.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 角色创建DTO
 */
@Data
@Schema(description = "角色创建请求参数")
public class RoleCreateDTO {

    @NotBlank(message = "角色编码不能为空")
    @Pattern(regexp = "^ROLE_[A-Z_]+$", message = "角色编码必须以ROLE_开头，只能包含大写字母和下划线")
    @Schema(description = "角色编码（如：ROLE_ADMIN, ROLE_MANAGER）", required = true, example = "ROLE_MANAGER")
    private String roleCode;

    @NotBlank(message = "角色名称不能为空")
    @Schema(description = "角色名称", required = true, example = "经理")
    private String roleName;

    @Schema(description = "角色描述", example = "商城经理，可管理商品和订单")
    private String description;

    @Schema(description = "排序", example = "1")
    private Integer sort;

    @Schema(description = "状态 0-禁用 1-启用", example = "1")
    private Integer status;
}
