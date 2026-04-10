package com.online.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 用户登录DTO
 */
@Data
@Schema(description = "用户登录请求参数")
public class UserLoginDTO {

    @NotBlank(message = "{javax.validation.constraints.NotBlank.message}")
    @Schema(description = "用户名", required = true, example = "zhangsan")
    private String username;

    @NotBlank(message = "{javax.validation.constraints.NotBlank.message}")
    @Schema(description = "密码", required = true, example = "123456")
    private String password;

    @Schema(description = "记住我", example = "true")
    private Boolean rememberMe = false;
}
