package com.online.mall.common;

import lombok.Getter;

/**
 * 业务异常类
 * 支持消息码和国际化
 */
@Getter
public class BusinessException extends RuntimeException {

    /** 消息码 */
    private final String code;

    /** 消息参数 */
    private final Object[] args;

    /**
     * 使用消息码构造
     */
    public BusinessException(String code) {
        this(code, null, null);
    }

    /**
     * 使用消息码和参数构造
     */
    public BusinessException(String code, Object[] args) {
        this(code, args, null);
    }

    /**
     * 使用消息码、参数和原因构造
     */
    public BusinessException(String code, Object[] args, Throwable cause) {
        super(I18nMessageUtil.getMessage(code, args), cause);
        this.code = code;
        this.args = args;
    }

    /**
     * 获取国际化消息
     */
    @Override
    public String getLocalizedMessage() {
        if (code != null) {
            return I18nMessageUtil.getMessage(code, args);
        }
        return super.getMessage();
    }
}
