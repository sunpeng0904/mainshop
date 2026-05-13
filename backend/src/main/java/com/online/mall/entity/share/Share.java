package com.online.mall.entity.share;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 分享实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("share")
public class Share {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 发布者ID
     */
    private Long userId;

    /**
     * 文本内容
     */
    private String content;

    /**
     * 所在位置
     */
    private String location;

    /**
     * 可见范围: 1公开 2好友 3部分可见 4不给谁看 5仅自己
     */
    private Integer visibility;

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
     * 状态: 0删除 1正常 2审核中
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer deleted;
}
