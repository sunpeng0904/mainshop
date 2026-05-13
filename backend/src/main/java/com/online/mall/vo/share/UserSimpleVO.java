package com.online.mall.vo.share;

import lombok.Data;

/**
 * 用户简单信息视图对象
 */
@Data
public class UserSimpleVO {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;
}
