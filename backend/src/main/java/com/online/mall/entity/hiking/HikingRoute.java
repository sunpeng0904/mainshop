package com.online.mall.entity.hiking;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 徒步路线实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_hiking_route")
public class HikingRoute {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 路线名称
     */
    private String name;

    /**
     * 路线简介
     */
    private String description;

    /**
     * 难度等级: easy-简单, medium-中等, hard-困难
     */
    private String difficulty;

    /**
     * 所在地区
     */
    private String location;

    /**
     * 距离(公里)
     */
    private BigDecimal distance;

    /**
     * 预计用时(小时)
     */
    private BigDecimal duration;

    /**
     * 累计爬升(米)
     */
    private Integer elevationGain;

    /**
     * 最高海拔(米)
     */
    private Integer maxElevation;

    /**
     * 最佳季节
     */
    private String bestSeason;

    /**
     * 路线标签,逗号分隔
     */
    private String tags;

    /**
     * 是否热门 0-否 1-是
     */
    private Integer isHot;

    /**
     * 封面图片URL
     */
    private String coverImage;

    /**
     * 详情图片URL列表,JSON格式
     */
    private String images;

    /**
     * 轨迹数据,JSON格式
     */
    private String trackData;

    /**
     * 行程安排,JSON格式
     */
    private String itinerary;

    /**
     * 装备建议,JSON格式
     */
    private String equipment;

    /**
     * 注意事项,JSON格式
     */
    private String warnings;

    /**
     * 评分 0-5
     */
    private BigDecimal rating;

    /**
     * 评价数量
     */
    private Integer reviewCount;

    /**
     * 状态: draft-草稿, published-已上线, offline-已下线
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
