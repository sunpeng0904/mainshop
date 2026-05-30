package com.online.mall.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.online.mall.service.ImageCacheService;

import lombok.extern.slf4j.Slf4j;

/**
 * 应用启动后预缓存图片"请用 Playwright 打开 https://www.baidu.com，在搜索框输入『Playwright MCP』，点击百度一下，然后截图给我。"
 */
@Slf4j
//@Componentclc
public class ImageCacheInitializer implements ApplicationRunner {

    @Autowired
    private ImageCacheService imageCacheService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("开始预缓存商品图片...");
        imageCacheService.preCacheImages();
        log.info("商品图片预缓存完成");
    }
}
