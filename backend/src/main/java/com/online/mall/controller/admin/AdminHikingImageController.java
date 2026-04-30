package com.online.mall.controller.admin;

import com.online.mall.common.Result;
import com.online.mall.service.hiking.HikingImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 后台管理 - 徒步路线图片管理Controller
 */
@RestController
@RequestMapping("/admin/hiking/images")
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AdminHikingImageController {

    @Autowired
    private HikingImageService hikingImageService;

    /**
     * 下载所有徒步路线图片到本地
     */
    @PostMapping("/download-all")
    public Result<Map<String, Object>> downloadAllImages() {
        log.info("管理员手动触发徒步路线图片下载");

        int downloaded = hikingImageService.downloadAllImages();

        Map<String, Object> result = new HashMap<>();
        result.put("downloaded", downloaded);
        result.put("message", "成功处理 " + downloaded + " 条路线的图片");

        return Result.success(result);
    }
}
