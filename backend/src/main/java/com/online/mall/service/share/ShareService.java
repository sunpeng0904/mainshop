package com.online.mall.service.share;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.online.mall.dto.share.ShareCreateDTO;
import com.online.mall.entity.share.Share;
import com.online.mall.vo.share.ShareVO;

/**
 * 分享服务接口
 */
public interface ShareService extends IService<Share> {

    /**
     * 发布分享
     */
    ShareVO createShare(Long userId, ShareCreateDTO dto);

    /**
     * 获取分享详情
     */
    ShareVO getShareDetail(Long shareId, Long currentUserId);

    /**
     * 获取我的分享列表
     */
    IPage<ShareVO> getMyShares(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 获取好友圈动态
     */
    IPage<ShareVO> getFriendShares(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 获取热门分享
     */
    IPage<ShareVO> getHotShares(Integer pageNum, Integer pageSize);

    /**
     * 获取用户分享列表
     */
    IPage<ShareVO> getUserShares(Long userId, Long currentUserId, Integer pageNum, Integer pageSize);

    /**
     * 删除分享
     */
    void deleteShare(Long shareId, Long userId);

    /**
     * 检查分享可见性
     */
    boolean checkVisibility(Long shareId, Long viewerId);
}
