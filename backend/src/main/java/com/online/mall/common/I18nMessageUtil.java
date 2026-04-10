package com.online.mall.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Locale;

/**
 * 国际化消息工具类
 */
@Component
public class I18nMessageUtil {

    @Autowired
    private MessageSource messageSource;

    private static I18nMessageUtil instance;

    @PostConstruct
    public void init() {
        instance = this;
    }

    /**
     * 获取国际化消息
     */
    public static String getMessage(String code) {
        return getMessage(code, null);
    }

    /**
     * 获取国际化消息（带参数）
     */
    public static String getMessage(String code, Object[] args) {
        return getMessage(code, args, code);
    }

    /**
     * 获取国际化消息（带参数和默认值）
     */
    public static String getMessage(String code, Object[] args, String defaultMessage) {
        try {
            Locale locale = LocaleContextHolder.getLocale();
            return instance.messageSource.getMessage(code, args, defaultMessage, locale);
        } catch (Exception e) {
            return defaultMessage != null ? defaultMessage : code;
        }
    }

    /**
     * 获取当前 Locale
     */
    public static Locale getCurrentLocale() {
        return LocaleContextHolder.getLocale();
    }

    /**
     * 判断当前是否为中文环境
     */
    public static boolean isChinese() {
        Locale locale = getCurrentLocale();
        return Locale.SIMPLIFIED_CHINESE.getLanguage().equals(locale.getLanguage());
    }
}
