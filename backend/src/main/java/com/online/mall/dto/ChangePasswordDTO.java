package com.online.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 修改密码DTO
 */
@Data
@Schema(description = "修改密码请求")
public class ChangePasswordDTO {

    @NotBlank(message = "{javax.validation.constraints.NotBlank.message}")
    @Size(min = 6, max = 20, message = "{javax.validation.constraints.Size.message}")
    @Schema(description = "旧密码", example = "123456")
    private String oldPassword;

    @NotBlank(message = "{javax.validation.constraints.NotBlank.message}")
    @Size(min = 6, max = 20, message = "{javax.validation.constraints.Size.message}")
    @Schema(description = "新密码", example = "newpassword")
    private String newPassword;
}
