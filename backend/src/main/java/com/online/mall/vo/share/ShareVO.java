package com.online.mall.vo.share;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 分享视图对象
 */
@Data
public class ShareVO {

    /**
     * 分享ID
     */
    private Long shareId;

    /**
     * 用户信息
     */
    private UserSimpleVO user;

    /**
     * 文本内容
     */
    private String content;

    /**
     * 图片URL列表
     */
    private List<String> imageUrls;

    /**
     * 所在位置
     */
    private String location;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 转发数
     */
    private Integer forwardCount;

    /**
     * 收藏数
     */
    private Integer collectCount;

    /**
     * 当前用户是否已点赞
     */
    private Boolean isLiked;

    /**
     * 当前用户是否已收藏
     */
    private Boolean isCollected;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
