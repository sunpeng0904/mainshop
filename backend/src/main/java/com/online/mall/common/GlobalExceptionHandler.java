package com.online.mall.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * 处理参数验证异常 (@Valid)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Locale locale = LocaleContextHolder.getLocale();
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> {
                    String fieldName = getFieldName(error.getField(), locale);
                    String errorMessage = error.getDefaultMessage();
                    // 如果消息包含占位符，尝试翻译
                    if (errorMessage != null && errorMessage.contains("{0}")) {
                        return errorMessage.replace("{0}", fieldName);
                    }
                    return fieldName + ": " + errorMessage;
                })
                .collect(Collectors.joining(", "));
        log.error("参数验证失败: {}", message);
        return Result.validateFailed(message);
    }

    /**
     * 处理参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleBindException(BindException e) {
        Locale locale = LocaleContextHolder.getLocale();
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> {
                    String fieldName = getFieldName(error.getField(), locale);
                    String errorMessage = error.getDefaultMessage();
                    if (errorMessage != null && errorMessage.contains("{0}")) {
                        return errorMessage.replace("{0}", fieldName);
                    }
                    return fieldName + ": " + errorMessage;
                })
                .collect(Collectors.joining(", "));
        log.error("参数绑定失败: {}", message);
        return Result.validateFailed(message);
    }

    /**
     * 处理参数验证异常 (@Validated)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleConstraintViolationException(ConstraintViolationException e) {
        Locale locale = LocaleContextHolder.getLocale();
        String message = e.getConstraintViolations().stream()
                .map(violation -> {
                    String fieldName = violation.getPropertyPath().toString();
                    String translatedField = getFieldName(fieldName, locale);
                    String errorMsg = violation.getMessage();
                    if (errorMsg != null && errorMsg.contains("{0}")) {
                        return errorMsg.replace("{0}", translatedField);
                    }
                    return translatedField + ": " + errorMsg;
                })
                .collect(Collectors.joining(", "));
        log.error("参数验证失败: {}", message);
        return Result.validateFailed(message);
    }

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.error("业务异常: {}", e.getMessage());
        return Result.businessError(e);
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        log.error("运行时异常: {} {}", request.getRequestURI(), e.getMessage(), e);
        return Result.error("common.error.system");
    }

    /**
     * 处理所有异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常: {} {}", request.getRequestURI(), e.getMessage(), e);
        return Result.error("common.error.system");
    }

    /**
     * 获取字段名称（翻译）
     */
    private String getFieldName(String field, Locale locale) {
        try {
            return messageSource.getMessage("field." + field, null, field, locale);
        } catch (Exception e) {
            return field;
        }
    }
}
