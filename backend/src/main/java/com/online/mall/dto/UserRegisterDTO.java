package com.online.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 用户注册DTO
 */
@Data
@Schema(description = "用户注册请求参数")
public class UserRegisterDTO {

    @NotBlank(message = "{javax.validation.constraints.NotBlank.message}")
    @Size(min = 3, max = 20, message = "{javax.validation.constraints.Size.message}")
    @Schema(description = "用户名", required = true, example = "zhangsan")
    private String username;

    @NotBlank(message = "{javax.validation.constraints.NotBlank.message}")
    @Size(min = 6, max = 20, message = "{javax.validation.constraints.Size.message}")
    @Schema(description = "密码", required = true, example = "123456")
    private String password;

    @NotBlank(message = "{javax.validation.constraints.NotBlank.message}")
    @Schema(description = "确认密码", required = true, example = "123456")
    private String confirmPassword;

    @Email(message = "{javax.validation.constraints.Email.message}")
    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "{javax.validation.constraints.Pattern.message}")
    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @Schema(description = "昵称", example = "张三")
    private String nickname;

    @Schema(description = "性别 0-未知 1-男 2-女", example = "1")
    private Integer gender;
}
