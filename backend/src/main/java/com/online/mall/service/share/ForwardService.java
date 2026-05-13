package com.online.mall.service.share;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.online.mall.dto.share.ForwardCreateDTO;
import com.online.mall.entity.share.ShareForward;
import com.online.mall.vo.share.ShareVO;

/**
 * 转发服务接口
 */
public interface ForwardService extends IService<ShareForward> {

    /**
     * 转发分享
     */
    ShareVO forwardShare(Long shareId, Long userId, ForwardCreateDTO dto);

    /**
     * 获取转发列表
     */
    IPage<ShareVO> getForwards(Long shareId, Integer pageNum, Integer pageSize);
}
