package com.online.mall.service.hiking;

import com.online.mall.entity.hiking.HikingRoute;

import java.util.List;

/**
 * 徒步路线图片服务
 */
public interface HikingImageService {

    /**
     * 下载所有徒步路线的图片到本地
     * @return 下载成功的图片数量
     */
    int downloadAllImages();

    /**
     * 下载指定路线的图片到本地
     * @param route 路线实体
     * @return 是否全部下载成功
     */
    boolean downloadRouteImages(HikingRoute route);

    /**
     * 更新路线图片URL为本地链接
     * @param routeId 路线ID
     * @param localCoverImage 本地封面图片路径
     * @param localImages 本地详情图片路径JSON
     */
    void updateRouteImageUrls(Long routeId, String localCoverImage, String localImages);

    /**
     * 获取本地图片访问URL
     * @param fileName 文件名
     * @return 本地访问URL
     */
    String getLocalImageUrl(String fileName);

    /**
     * 检查本地图片是否存在
     * @param fileName 文件名
     * @return 是否存在
     */
    boolean imageExists(String fileName);
}
