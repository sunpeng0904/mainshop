package com.online.mall.dto.share;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 分享查询DTO
 */
@Data
public class ShareQueryDTO {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 查询类型: my(我的), friend(好友圈), hot(热门)
     */
    private String type;

    @Min(value = 1, message = "页码最小为1")
    private Integer pageNum = 1;

    @Min(value = 1)
    @Max(value = 50)
    private Integer pageSize = 10;
}
