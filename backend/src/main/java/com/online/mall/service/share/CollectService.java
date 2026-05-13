package com.online.mall.service.share;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.online.mall.entity.share.ShareCollect;
import com.online.mall.vo.share.ShareVO;

import java.util.List;
import java.util.Map;

/**
 * 收藏服务接口
 */
public interface CollectService extends IService<ShareCollect> {

    /**
     * 收藏/取消收藏
     */
    Map<String, Object> toggleCollect(Long shareId, Long userId);

    /**
     * 检查是否已收藏
     */
    boolean isCollected(Long shareId, Long userId);

    /**
     * 批量检查是否已收藏
     */
    Map<Long, Boolean> batchCheckCollected(List<Long> shareIds, Long userId);

    /**
     * 获取收藏列表
     */
    IPage<ShareVO> getCollects(Long userId, Integer pageNum, Integer pageSize);
}
