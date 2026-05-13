package com.online.mall.entity.share;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 分享收藏实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("share_collect")
public class ShareCollect {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 分享ID
     */
    private Long shareId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 收藏夹ID，0表示默认收藏夹
     */
    private Long folderId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
