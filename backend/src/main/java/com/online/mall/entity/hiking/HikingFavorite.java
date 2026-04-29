package com.online.mall.entity.hiking;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 路线收藏实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_hiking_favorite")
public class HikingFavorite {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 路线ID
     */
    private Long routeId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 删除标志 0-未删除 1-已删除
     */
    @TableLogic
    private Integer deleted;
}
