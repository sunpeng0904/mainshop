package com.online.mall.entity.share;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 分享图片实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("share_image")
public class ShareImage {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 分享ID
     */
    private Long shareId;

    /**
     * 原图URL
     */
    private String imageUrl;

    /**
     * 缩略图URL
     */
    private String thumbUrl;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
