package com.online.mall.dto.share;

import lombok.Data;

import javax.validation.constraints.Size;

/**
 * 转发创建DTO
 */
@Data
public class ForwardCreateDTO {

    /**
     * 转发评论
     */
    @Size(max = 500, message = "转发评论最多500字")
    private String content;

    /**
     * 转发时是否同时点赞
     */
    private Boolean alsoLike = false;
}
