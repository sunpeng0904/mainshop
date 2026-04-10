package com.online.mall.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一返回结果
 * 支持国际化消息
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 状态码 */
    private Integer code;

    /** 返回消息码（用于前端国际化） */
    private String messageCode;

    /** 返回消息（已国际化） */
    private String message;

    /** 返回数据 */
    private T data;

    /** 时间戳 */
    private Long timestamp;

    public Result() {
        this.timestamp = System.currentTimeMillis();
    }

    public Result(Integer code, String messageCode, String message, T data) {
        this.code = code;
        this.messageCode = messageCode;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    // ==================== 成功方法 ====================

    /**
     * 成功返回结果（使用默认成功消息）
     */
    public static <T> Result<T> success() {
        return success(null, "common.success");
    }

    public static <T> Result<T> success(T data) {
        return success(data, "common.success");
    }

    /**
     * 成功返回结果（指定消息码）
     */
    public static <T> Result<T> success(T data, String messageCode) {
        String message = I18nMessageUtil.getMessage(messageCode);
        return new Result<>(200, messageCode, message, data);
    }

    /**
     * 成功返回结果（带参数）
     */
    public static <T> Result<T> success(T data, String messageCode, Object[] args) {
        String message = I18nMessageUtil.getMessage(messageCode, args);
        return new Result<>(200, messageCode, message, data);
    }

    // ==================== 失败方法 ====================

    /**
     * 失败返回结果（默认错误消息）
     */
    public static <T> Result<T> error() {
        return error(500, "common.error");
    }

    public static <T> Result<T> error(String messageCode) {
        return error(500, messageCode);
    }

    public static <T> Result<T> error(Integer code, String messageCode) {
        String message = I18nMessageUtil.getMessage(messageCode);
        return new Result<>(code, messageCode, message, null);
    }

    /**
     * 失败返回结果（带参数）
     */
    public static <T> Result<T> error(Integer code, String messageCode, Object[] args) {
        String message = I18nMessageUtil.getMessage(messageCode, args);
        return new Result<>(code, messageCode, message, null);
    }

    // ==================== 参数验证失败 ====================

    public static <T> Result<T> validateFailed() {
        return error(400, "common.param.invalid");
    }

    public static <T> Result<T> validateFailed(String message) {
        return new Result<>(400, "common.param.invalid", message, null);
    }

    // ==================== 未登录 ====================

    public static <T> Result<T> unauthorized() {
        return error(401, "common.unauthorized");
    }

    public static <T> Result<T> unauthorized(String messageCode) {
        String message = I18nMessageUtil.getMessage(messageCode);
        return new Result<>(401, messageCode, message, null);
    }

    // ==================== 未授权 ====================

    public static <T> Result<T> forbidden() {
        return error(403, "common.forbidden");
    }

    public static <T> Result<T> forbidden(String messageCode) {
        String message = I18nMessageUtil.getMessage(messageCode);
        return new Result<>(403, messageCode, message, null);
    }

    // ==================== 业务异常 ====================

    /**
     * 业务异常（使用消息码）
     */
    public static <T> Result<T> businessError(String messageCode) {
        String message = I18nMessageUtil.getMessage(messageCode);
        return new Result<>(1000, messageCode, message, null);
    }

    /**
     * 业务异常（使用消息码和参数）
     */
    public static <T> Result<T> businessError(String messageCode, Object[] args) {
        String message = I18nMessageUtil.getMessage(messageCode, args);
        return new Result<>(1000, messageCode, message, null);
    }

    /**
     * 业务异常（使用 BusinessException 构造）
     */
    public static <T> Result<T> businessError(BusinessException e) {
        String message = e.getLocalizedMessage();
        return new Result<>(1000, e.getCode(), message, null);
    }

    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return code != null && code == 200;
    }
}
