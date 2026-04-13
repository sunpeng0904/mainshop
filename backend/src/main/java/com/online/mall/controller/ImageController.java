package com.online.mall.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.online.mall.service.ImageCacheService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * 图片控制器
 */
@Slf4j
@RestController
@RequestMapping("/image")
@Tag(name = "图片服务", description = "图片相关接口")
public class ImageController {

    @Autowired
    private ImageCacheService imageCacheService;

    @Operation(summary = "获取缓存图片")
    @GetMapping("/cache/{imageKey}")
    public ResponseEntity<Resource> getCachedImage(
            @PathVariable String imageKey,
            @RequestParam(defaultValue = "400") int width,
            @RequestParam(defaultValue = "400") int height) {

        try {
            String cachedPath = imageCacheService.getCacheImagePath(imageKey, width, height);
            if (cachedPath != null) {
                return imageCacheService.getImageResource(cachedPath);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("获取缓存图片失败: {}", imageKey, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "获取本地图片")
    @GetMapping("/**")
    public ResponseEntity<Resource> getLocalImage(HttpServletRequest request) {
        try {
            String path = request.getRequestURI().substring("/api/image".length());
            return imageCacheService.getImageResource(path);
        } catch (Exception e) {
            log.error("获取本地图片失败", e);
            return ResponseEntity.notFound().build();
        }
    }
}
