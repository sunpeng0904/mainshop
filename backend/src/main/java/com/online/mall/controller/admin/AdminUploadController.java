package com.online.mall.controller.admin;

import com.online.mall.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 管理员文件上传控制器
 */
@Slf4j
@RestController
@RequestMapping("/admin/upload")
@Tag(name = "管理员-文件上传", description = "文件上传接口")
public class AdminUploadController {

    @Value("${app.file.upload-path:./uploads/}")
    private String configuredUploadPath;

    private String uploadPath;

    @PostConstruct
    public void init() {
        // 将相对路径转换为绝对路径
        Path path = Paths.get(configuredUploadPath);
        if (!path.isAbsolute()) {
            path = path.toAbsolutePath();
        }
        uploadPath = path.toString();
        // 确保路径以分隔符结尾
        if (!uploadPath.endsWith(File.separator)) {
            uploadPath += File.separator;
        }
        log.info("文件上传路径: {}", uploadPath);
    }

    // 允许的图片格式
    private static final String[] ALLOWED_IMAGE_TYPES = {
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    };

    // 允许的图片扩展名
    private static final String[] ALLOWED_IMAGE_EXTENSIONS = {
        ".jpg", ".jpeg", ".png", ".gif", ".webp"
    };

    // 最大文件大小 10MB
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    @Operation(summary = "上传图片")
    @PostMapping("/image")
    public Result<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        // 检查文件是否为空
        if (file.isEmpty()) {
            return Result.error(400, "上传文件不能为空");
        }

        // 检查文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            return Result.error(400, "文件大小不能超过10MB");
        }

        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            return Result.error(400, "文件名不能为空");
        }

        // 获取文件扩展名
        String extension = getFileExtension(originalFilename);
        if (extension == null || !isAllowedExtension(extension)) {
            return Result.error(400, "只支持 jpg、jpeg、png、gif、webp 格式的图片");
        }

        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !isAllowedContentType(contentType)) {
            return Result.error(400, "只支持 jpg、jpeg、png、gif、webp 格式的图片");
        }

        try {
            // 生成存储路径：uploads/images/yyyy/MM/
            LocalDate now = LocalDate.now();
            String datePath = now.format(DateTimeFormatter.ofPattern("yyyy/MM"));
            String targetDir = uploadPath + "images/" + datePath + "/";

            // 确保目录存在
            Path dirPath = Paths.get(targetDir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            // 生成新文件名
            String newFilename = UUID.randomUUID().toString().replace("-", "") + extension;
            String filePath = targetDir + newFilename;

            // 保存文件
            file.transferTo(new File(filePath));

            log.info("图片上传成功: {}", filePath);

            // 返回访问路径
            String accessPath = "/uploads/images/" + datePath + "/" + newFilename;
            Map<String, String> result = new HashMap<>();
            result.put("url", accessPath);
            result.put("name", originalFilename);

            return Result.success(result, "上传成功");
        } catch (IOException e) {
            log.error("图片上传失败", e);
            return Result.error(500, "图片上传失败: " + e.getMessage());
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return null;
        }
        return filename.substring(lastDotIndex).toLowerCase();
    }

    /**
     * 检查是否为允许的扩展名
     */
    private boolean isAllowedExtension(String extension) {
        for (String allowed : ALLOWED_IMAGE_EXTENSIONS) {
            if (allowed.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查是否为允许的内容类型
     */
    private boolean isAllowedContentType(String contentType) {
        for (String allowed : ALLOWED_IMAGE_TYPES) {
            if (allowed.equalsIgnoreCase(contentType)) {
                return true;
            }
        }
        return false;
    }
}
