package com.online.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 用户更新DTO
 */
@Data
@Schema(description = "用户更新请求参数")
public class UserUpdateDTO {

    @Size(min = 3, max = 20, message = "用户名长度必须在3-20个字符之间")
    @Schema(description = "用户名", example = "zhangsan")
    private String username;

    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    @Schema(description = "密码（如需修改密码请同时填写confirmPassword）", example = "123456")
    private String password;

    @Schema(description = "确认密码（修改密码时必填）", example = "123456")
    private String confirmPassword;

    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @Schema(description = "昵称", example = "张三")
    private String nickname;

    @Schema(description = "性别 0-未知 1-男 2-女", example = "1")
    private Integer gender;
}
