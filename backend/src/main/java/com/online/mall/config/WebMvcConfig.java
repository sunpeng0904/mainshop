package com.online.mall.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * Web MVC 配置
 */
@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${app.file.upload-path:./uploads/}")
    private String uploadPath;

    /**
     * 配置静态资源映射
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("配置静态资源映射: uploadPath={}", uploadPath);

        // 确保上传路径以分隔符结尾
        String normalizedUploadPath = uploadPath;
        if (!normalizedUploadPath.endsWith(File.separator)) {
            normalizedUploadPath += File.separator;
        }

        // 映射上传文件目录
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + normalizedUploadPath);

        // 映射静态资源目录（images等）
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");

        log.info("静态资源映射配置完成: /uploads/** -> file:{}, /images/** -> classpath:/static/images/", normalizedUploadPath);
    }
}
