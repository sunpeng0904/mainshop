package com.online.mall.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.extern.slf4j.Slf4j;

/**
 * Web MVC 配置
 */
@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${app.file.upload-path:./uploads/}")
    private String uploadPath;

    @Value("${app.image.cache-path:./uploads/cache/}")
    private String cachePath;

    @Value("${app.image.products-path:./src/main/resources/static/images/products/}")
    private String productsPath;

    /**
     * 配置静态资源映射
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("配置静态资源映射: uploadPath={}, cachePath={}, productsPath={}", uploadPath, cachePath, productsPath);

        // 映射上传文件目录
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath);

        // 映射图片缓存目录
        registry.addResourceHandler("/uploads/cache/**")
                .addResourceLocations("file:" + cachePath);

        // 映射商品图片目录（优先从文件系统加载，支持动态下载）
        registry.addResourceHandler("/images/products/**")
                .addResourceLocations("file:" + productsPath);

        // 映射静态资源目录（其他静态图片等）
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");
    }
}
