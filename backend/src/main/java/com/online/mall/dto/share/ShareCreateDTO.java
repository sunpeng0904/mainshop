package com.online.mall.dto.share;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 分享创建DTO
 */
@Data
public class ShareCreateDTO {

    /**
     * 文本内容
     */
    @NotBlank(message = "内容不能为空")
    @Size(max = 1000, message = "内容最多1000字")
    private String content;

    /**
     * 图片URL列表，最多9张
     */
    @Size(max = 9, message = "最多上传9张图片")
    private List<String> imageUrls;

    /**
     * 所在位置
     */
    private String location;

    /**
     * 可见范围，默认1（公开）
     */
    private Integer visibility = 1;

    /**
     * @的用户ID列表
     */
    private List<Long> mentionUserIds;
}
