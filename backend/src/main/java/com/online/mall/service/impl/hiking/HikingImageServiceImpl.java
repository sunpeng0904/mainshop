package com.online.mall.service.impl.hiking;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.online.mall.entity.hiking.HikingRoute;
import com.online.mall.mapper.hiking.HikingRouteMapper;
import com.online.mall.service.hiking.HikingImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 徒步路线图片服务实现类
 */
@Slf4j
@Service
public class HikingImageServiceImpl implements HikingImageService {

    @Autowired
    private HikingRouteMapper hikingRouteMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${app.image.hiking-path:./src/main/resources/static/images/hiking/}")
    private String hikingImagePath;

    @Value("${app.image.hiking-url:/images/hiking/}")
    private String hikingImageUrl;

    // 下载超时时间（毫秒）
    private static final int DOWNLOAD_TIMEOUT = 30000;

    // 是否自动下载图片（可通过配置控制）
    @Value("${app.image.auto-download:false}")
    private boolean autoDownload;

    /**
     * 应用启动时自动下载图片
     */
    @PostConstruct
    public void init() {
        if (autoDownload) {
            log.info("自动下载徒步路线图片功能已开启");
            downloadAllImages();
        }
    }

    @Override
    public int downloadAllImages() {
        log.info("开始下载所有徒步路线图片...");
        int totalDownloaded = 0;

        try {
            // 确保目录存在
            Path dir = Paths.get(hikingImagePath);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
                log.info("创建徒步路线图片目录: {}", dir);
            }

            // 获取所有路线
            List<HikingRoute> routes = hikingRouteMapper.selectList(new QueryWrapper<>());
            log.info("共有 {} 条徒步路线需要处理", routes.size());

            for (HikingRoute route : routes) {
                try {
                    boolean success = downloadRouteImages(route);
                    if (success) {
                        totalDownloaded++;
                    }
                } catch (Exception e) {
                    log.error("下载路线 {} 图片失败: {}", route.getId(), e.getMessage());
                }
            }

            log.info("徒步路线图片下载完成，成功处理 {} 条路线", totalDownloaded);
        } catch (Exception e) {
            log.error("下载徒步路线图片时发生错误", e);
        }

        return totalDownloaded;
    }

    @Override
    public boolean downloadRouteImages(HikingRoute route) {
        if (route == null) {
            return false;
        }

        boolean allSuccess = true;
        Long routeId = route.getId();

        try {
            // 处理封面图片
            String coverImage = route.getCoverImage();
            if (StringUtils.hasText(coverImage) && isRemoteUrl(coverImage)) {
                String localFileName = "route_" + routeId + "_cover.jpg";
                boolean coverSuccess = downloadImage(coverImage, localFileName);

                if (coverSuccess) {
                    // 更新数据库为本地链接
                    String localUrl = hikingImageUrl + localFileName;
                    route.setCoverImage(localUrl);
                } else {
                    allSuccess = false;
                }
            }

            // 处理详情图片
            String images = route.getImages();
            if (StringUtils.hasText(images)) {
                List<String> imageUrls = objectMapper.readValue(images, new TypeReference<List<String>>() {});
                List<String> localImageUrls = new ArrayList<>();

                int index = 1;
                for (String imageUrl : imageUrls) {
                    if (isRemoteUrl(imageUrl)) {
                        String localFileName = "route_" + routeId + "_" + index + ".jpg";
                        boolean imgSuccess = downloadImage(imageUrl, localFileName);

                        if (imgSuccess) {
                            localImageUrls.add(hikingImageUrl + localFileName);
                        } else {
                            // 下载失败，保留原链接
                            localImageUrls.add(imageUrl);
                            allSuccess = false;
                        }
                        index++;
                    } else {
                        // 已经是本地链接
                        localImageUrls.add(imageUrl);
                    }
                }

                // 更新图片列表
                route.setImages(objectMapper.writeValueAsString(localImageUrls));
            }

            // 更新数据库
            hikingRouteMapper.updateById(route);
            log.info("路线 {} 图片处理完成", routeId);

        } catch (Exception e) {
            log.error("处理路线 {} 图片失败: {}", routeId, e.getMessage());
            allSuccess = false;
        }

        return allSuccess;
    }

    /**
     * 下载单个图片
     */
    private boolean downloadImage(String imageUrl, String fileName) {
        // 如果已经是本地链接，直接返回成功
        if (!isRemoteUrl(imageUrl)) {
            return true;
        }

        try {
            Path filePath = Paths.get(hikingImagePath).resolve(fileName);

            // 如果文件已存在，跳过下载
            if (Files.exists(filePath)) {
                log.debug("图片已存在，跳过下载: {}", fileName);
                return true;
            }

            log.info("下载图片: {} -> {}", imageUrl, filePath);

            // 创建连接
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(DOWNLOAD_TIMEOUT);
            connection.setReadTimeout(DOWNLOAD_TIMEOUT);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                log.error("下载图片失败，HTTP状态码: {}", responseCode);
                return false;
            }

            // 下载文件
            try (InputStream in = connection.getInputStream();
                 FileOutputStream out = new FileOutputStream(filePath.toFile())) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }

            connection.disconnect();
            log.info("图片下载完成: {}", fileName);
            return true;

        } catch (Exception e) {
            log.error("下载图片失败: {} - {}", imageUrl, e.getMessage());
            return false;
        }
    }

    /**
     * 判断URL是否为远程链接
     */
    private boolean isRemoteUrl(String url) {
        if (!StringUtils.hasText(url)) {
            return false;
        }
        return url.startsWith("http://") || url.startsWith("https://");
    }

    @Override
    public void updateRouteImageUrls(Long routeId, String localCoverImage, String localImages) {
        HikingRoute route = new HikingRoute();
        route.setId(routeId);
        route.setCoverImage(localCoverImage);
        route.setImages(localImages);
        hikingRouteMapper.updateById(route);
        log.info("更新路线 {} 图片链接为本地链接", routeId);
    }

    @Override
    public String getLocalImageUrl(String fileName) {
        return hikingImageUrl + fileName;
    }

    @Override
    public boolean imageExists(String fileName) {
        Path filePath = Paths.get(hikingImagePath).resolve(fileName);
        return Files.exists(filePath);
    }
}
