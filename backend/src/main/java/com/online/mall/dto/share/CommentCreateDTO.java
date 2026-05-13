package com.online.mall.dto.share;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 评论创建DTO
 */
@Data
public class CommentCreateDTO {

    /**
     * 评论内容
     */
    @NotBlank(message = "评论内容不能为空")
    @Size(max = 500, message = "评论最多500字")
    private String content;

    /**
     * 父评论ID，0表示一级评论
     */
    private Long parentId = 0L;

    /**
     * 回复的用户ID
     */
    private Long replyUserId = 0L;
}
