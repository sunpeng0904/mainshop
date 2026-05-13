package com.online.mall.service.share;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.online.mall.entity.share.ShareLike;
import com.online.mall.vo.share.UserSimpleVO;

import java.util.List;
import java.util.Map;

/**
 * 点赞服务接口
 */
public interface LikeService extends IService<ShareLike> {

    /**
     * 点赞/取消点赞
     */
    Map<String, Object> toggleLike(Long shareId, Long userId);

    /**
     * 检查是否已点赞
     */
    boolean isLiked(Long shareId, Long userId);

    /**
     * 批量检查是否已点赞
     */
    Map<Long, Boolean> batchCheckLiked(List<Long> shareIds, Long userId);

    /**
     * 获取点赞列表
     */
    IPage<UserSimpleVO> getLikeUsers(Long shareId, Integer pageNum, Integer pageSize);
}
