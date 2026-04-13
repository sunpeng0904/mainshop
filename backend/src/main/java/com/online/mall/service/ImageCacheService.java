package com.online.mall.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 图片缓存服务
 * 负责下载和管理商品图片到本地目录
 */
@Slf4j
@Service
public class ImageCacheService {

    @Value("${app.file.upload-path:./uploads/}")
    private String uploadPath;

    @Value("${app.image.cache-path:./uploads/cache/}")
    private String cachePath;

    @Value("${app.image.products-path:./src/main/resources/static/images/products/}")
    private String productsPath;

    private static final String IMAGE_BASE_URL = "https://picsum.photos/seed/";

    // 商品ID到图片URL的映射
    private static final Map<Long, String[]> PRODUCT_IMAGE_MAPPING = new HashMap<>();

    static {
        // 手机数码类 - 使用不同seed获取不同图片
        PRODUCT_IMAGE_MAPPING.put(1L, new String[]{"iphone15", "iphone15_2"});  // iPhone 15 Pro Max
        PRODUCT_IMAGE_MAPPING.put(2L, new String[]{"huawei60", "huawei60_2"});  // 华为 Mate 60 Pro
        PRODUCT_IMAGE_MAPPING.put(3L, new String[]{"xiaomi14", "xiaomi14_2"});  // 小米14 Ultra

        // 电脑办公类
        PRODUCT_IMAGE_MAPPING.put(4L, new String[]{"macbook14", "macbook14_2"});  // MacBook Pro
        PRODUCT_IMAGE_MAPPING.put(5L, new String[]{"thinkpad", "thinkpad_2"});    // ThinkPad X1

        // 手机配件类
        PRODUCT_IMAGE_MAPPING.put(6L, new String[]{"airpods", "airpods_2"});      // AirPods Pro
        PRODUCT_IMAGE_MAPPING.put(7L, new String[]{"sonywh", "sonywh_2"});        // Sony 耳机

        // 家用电器类
        PRODUCT_IMAGE_MAPPING.put(8L, new String[]{"dyson", "dyson_2"});          // 戴森吸尘器
        PRODUCT_IMAGE_MAPPING.put(9L, new String[]{"midea", "midea_2"});          // 美的空调
        PRODUCT_IMAGE_MAPPING.put(10L, new String[]{"siemens", "siemens_2"});     // 西门子冰箱

        // 电脑配件类
        PRODUCT_IMAGE_MAPPING.put(11L, new String[]{"rtx4090", "rtx4090_2"});     // RTX 4090
        PRODUCT_IMAGE_MAPPING.put(12L, new String[]{"samsungssd", "samsungssd_2"}); // Samsung SSD
    }

    /**
     * 下载图片到本地静态资源目录
     */
    public boolean downloadImageToLocal(String seed, String fileName) {
        try {
            // 确保目录存在
            Path productsDir = Paths.get(productsPath);
            if (!Files.exists(productsDir)) {
                Files.createDirectories(productsDir);
            }

            Path filePath = productsDir.resolve(fileName);

            // 如果文件已存在，跳过下载
            if (Files.exists(filePath)) {
                log.debug("图片已存在: {}", fileName);
                return true;
            }

            // 下载图片
            String url = IMAGE_BASE_URL + seed + "/400/400";
            log.info("下载图片: {} -> {}", url, filePath);

            try (InputStream in = new URL(url).openStream();
                 FileOutputStream out = new FileOutputStream(filePath.toFile())) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }

            log.info("图片下载完成: {}", fileName);
            return true;
        } catch (Exception e) {
            log.error("下载图片失败: {} - {}", seed, e.getMessage());
            return false;
        }
    }

    /**
     * 预缓存所有商品图片到本地
     */
    public void preCacheImages() {
        log.info("开始预缓存商品图片到本地目录...");
        try {
            // 确保目录存在
            Path productsDir = Paths.get(productsPath);
            if (!Files.exists(productsDir)) {
                Files.createDirectories(productsDir);
            }

            // 下载所有商品图片
            for (Map.Entry<Long, String[]> entry : PRODUCT_IMAGE_MAPPING.entrySet()) {
                Long productId = entry.getKey();
                String[] seeds = entry.getValue();

                for (int i = 0; i < seeds.length; i++) {
                    String fileName = "product_" + productId + "_" + (i + 1) + ".jpg";
                    downloadImageToLocal(seeds[i], fileName);
                }
            }

            log.info("商品图片预缓存完成");
        } catch (Exception e) {
            log.error("预缓存图片失败", e);
        }
    }

    /**
     * 检查本地图片是否存在
     */
    public boolean imageExists(String relativePath) {
        if (!StringUtils.hasText(relativePath)) {
            return false;
        }

        // 移除前导斜杠
        if (relativePath.startsWith("/")) {
            relativePath = relativePath.substring(1);
        }

        // 处理 images/products/ 路径
        if (relativePath.startsWith("images/products/")) {
            Path filePath = Paths.get(productsPath).resolve(relativePath.substring("images/products/".length()));
            return Files.exists(filePath);
        }

        // 处理 uploads/cache/ 路径
        if (relativePath.startsWith("uploads/cache/")) {
            Path filePath = Paths.get(cachePath).resolve(relativePath.substring("uploads/cache/".length()));
            return Files.exists(filePath);
        }

        // 处理 uploads/ 路径
        if (relativePath.startsWith("uploads/")) {
            Path filePath = Paths.get(uploadPath).resolve(relativePath.substring("uploads/".length()));
            return Files.exists(filePath);
        }

        return false;
    }

    /**
     * 获取图片资源
     */
    public ResponseEntity<Resource> getImageResource(String relativePath) throws IOException {
        if (!StringUtils.hasText(relativePath)) {
            return ResponseEntity.notFound().build();
        }

        // 移除前导斜杠
        if (relativePath.startsWith("/")) {
            relativePath = relativePath.substring(1);
        }

        Path filePath = null;

        // 处理 images/products/ 路径（本地静态资源）
        if (relativePath.startsWith("images/products/")) {
            filePath = Paths.get(productsPath).resolve(relativePath.substring("images/products/".length()));
        }
        // 处理 uploads/cache/ 路径
        else if (relativePath.startsWith("uploads/cache/")) {
            filePath = Paths.get(cachePath).resolve(relativePath.substring("uploads/cache/".length()));
        }
        // 处理 uploads/ 路径
        else if (relativePath.startsWith("uploads/")) {
            filePath = Paths.get(uploadPath).resolve(relativePath.substring("uploads/".length()));
        }
        // 其他路径
        else {
            filePath = Paths.get(uploadPath).resolve(relativePath);
        }

        if (filePath == null || !Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new UrlResource(filePath.toUri());
        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = "image/jpeg";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    /**
     * 获取缓存图片路径（兼容旧接口）
     */
    public String getCacheImagePath(String imageKey, int width, int height) {
        try {
            // 确保缓存目录存在
            Path cacheDir = Paths.get(cachePath);
            if (!Files.exists(cacheDir)) {
                Files.createDirectories(cacheDir);
            }

            // 生成缓存文件名
            String fileName = imageKey + "_" + width + "x" + height + ".jpg";
            Path filePath = cacheDir.resolve(fileName);

            // 如果文件不存在，下载图片
            if (!Files.exists(filePath)) {
                downloadImage(imageKey, width, height, filePath);
            }

            return "/uploads/cache/" + fileName;
        } catch (Exception e) {
            log.error("缓存图片失败: {}", imageKey, e);
            return null;
        }
    }

    private void downloadImage(String seed, int width, int height, Path targetPath) throws IOException {
        String url = IMAGE_BASE_URL + seed + "/" + width + "/" + height;
        log.info("下载图片: {} -> {}", url, targetPath);
        try (InputStream in = new URL(url).openStream();
             FileOutputStream out = new FileOutputStream(targetPath.toFile())) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
        log.info("图片下载完成: {}", targetPath);
    }
}
