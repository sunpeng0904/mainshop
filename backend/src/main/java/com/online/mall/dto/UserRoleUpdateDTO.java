package com.online.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 用户角色更新DTO
 */
@Data
@Schema(description = "用户角色更新请求参数")
public class UserRoleUpdateDTO {

    @NotNull(message = "角色ID列表不能为空")
    @Schema(description = "角色ID列表", required = true, example = "[1, 2]")
    private List<Long> roleIds;
}
