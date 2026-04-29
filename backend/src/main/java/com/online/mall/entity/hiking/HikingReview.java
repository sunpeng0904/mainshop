package com.online.mall.entity.hiking;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 路线评价实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_hiking_review")
public class HikingReview {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 路线ID
     */
    private Long routeId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户昵称(冗余)
     */
    private String username;

    /**
     * 用户头像(冗余)
     */
    private String avatar;

    /**
     * 评分 1-5
     */
    private Integer rating;

    /**
     * 评价内容
     */
    private String content;

    /**
     * 评价图片,JSON格式
     */
    private String images;

    /**
     * 商家回复
     */
    private String reply;

    /**
     * 回复时间
     */
    private LocalDateTime replyTime;

    /**
     * 状态: pending-待审核, approved-已通过, rejected-已拒绝
     */
    private String status;

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
     * 删除标志 0-未删除 1-已删除
     */
    @TableLogic
    private Integer deleted;
}
