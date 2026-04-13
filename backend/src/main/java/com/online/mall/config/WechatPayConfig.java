package com.online.mall.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 微信支付配置
 * 文档: https://pay.weixin.qq.com/wiki/doc/apiv3/apis/chapter3_4_1.shtml
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "wechat.pay")
public class WechatPayConfig {

    /**
     * 应用ID (小程序/公众号的appid)
     */
    private String appId;

    /**
     * 商户号
     */
    private String mchId;

    /**
     * 商户API私钥路径
     */
    private String privateKeyPath;

    /**
     * 商户证书序列号
     */
    private String merchantSerialNumber;

    /**
     * API v3密钥
     */
    private String apiV3Key;

    /**
     * 回调地址
     */
    private String notifyUrl;

    /**
     * 是否沙箱环境
     */
    private boolean sandbox = true;

    /**
     * 获取沙箱模式的模拟配置
     */
    public static WechatPayConfig getSandboxConfig() {
        WechatPayConfig config = new WechatPayConfig();
        config.setAppId("wx1234567890abcdef");  // 沙箱appid
        config.setMchId("1234567890");          // 沙箱商户号
        config.setSandbox(true);
        config.setNotifyUrl("http://localhost:8082/api/payment/wechat/callback");
        return config;
    }
}
