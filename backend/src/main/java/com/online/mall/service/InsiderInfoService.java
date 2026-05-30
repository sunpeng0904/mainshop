package com.online.mall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.online.mall.entity.InsiderInfo;

/**
 * 内幕信息知情人登记Service接口
 */
public interface InsiderInfoService extends IService<InsiderInfo> {

    /**
     * 分页查询内幕信息知情人列表
     */
    Page<InsiderInfo> getInsiderList(Integer page, Integer pageSize, String companyName, String industry,
                                     String board, String financingType, String acceptTimeStart,
                                     String acceptTimeEnd, String knowledgeTimeStart, String knowledgeTimeEnd,
                                     String registerTimeStart, String registerTimeEnd, String insiderName,
                                     String reason, String content, String status);

    /**
     * 创建内幕信息知情人登记
     */
    boolean createInsider(InsiderInfo insiderInfo);

    /**
     * 更新内幕信息知情人登记
     */
    boolean updateInsider(InsiderInfo insiderInfo);

    /**
     * 删除内幕信息知情人登记
     */
    boolean deleteInsider(Long id);

    /**
     * 更新状态
     */
    boolean updateStatus(Long id, String status);
}
