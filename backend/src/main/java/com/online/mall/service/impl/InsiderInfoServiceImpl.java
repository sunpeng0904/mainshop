package com.online.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.online.mall.entity.InsiderInfo;
import com.online.mall.mapper.InsiderInfoMapper;
import com.online.mall.service.InsiderInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 内幕信息知情人登记Service实现类
 */
@Service
public class InsiderInfoServiceImpl extends ServiceImpl<InsiderInfoMapper, InsiderInfo> implements InsiderInfoService {

    @Override
    public Page<InsiderInfo> getInsiderList(Integer page, Integer pageSize, String companyName, String industry,
                                            String board, String financingType, String acceptTimeStart,
                                            String acceptTimeEnd, String knowledgeTimeStart, String knowledgeTimeEnd,
                                            String registerTimeStart, String registerTimeEnd, String insiderName,
                                            String reason, String content, String status) {
        Page<InsiderInfo> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<InsiderInfo> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(companyName)) {
            wrapper.like(InsiderInfo::getCompanyName, companyName);
        }
        if (StringUtils.isNotBlank(industry)) {
            wrapper.eq(InsiderInfo::getIndustry, industry);
        }
        if (StringUtils.isNotBlank(board)) {
            wrapper.eq(InsiderInfo::getBoard, board);
        }
        if (StringUtils.isNotBlank(financingType)) {
            wrapper.eq(InsiderInfo::getFinancingType, financingType);
        }
        if (StringUtils.isNotBlank(acceptTimeStart)) {
            wrapper.ge(InsiderInfo::getAcceptTime, LocalDate.parse(acceptTimeStart));
        }
        if (StringUtils.isNotBlank(acceptTimeEnd)) {
            wrapper.le(InsiderInfo::getAcceptTime, LocalDate.parse(acceptTimeEnd));
        }
        if (StringUtils.isNotBlank(knowledgeTimeStart)) {
            wrapper.ge(InsiderInfo::getKnowledgeTime, LocalDate.parse(knowledgeTimeStart));
        }
        if (StringUtils.isNotBlank(knowledgeTimeEnd)) {
            wrapper.le(InsiderInfo::getKnowledgeTime, LocalDate.parse(knowledgeTimeEnd));
        }
        if (StringUtils.isNotBlank(registerTimeStart)) {
            wrapper.ge(InsiderInfo::getRegisterTime, LocalDate.parse(registerTimeStart));
        }
        if (StringUtils.isNotBlank(registerTimeEnd)) {
            wrapper.le(InsiderInfo::getRegisterTime, LocalDate.parse(registerTimeEnd));
        }
        if (StringUtils.isNotBlank(insiderName)) {
            wrapper.like(InsiderInfo::getInsiderName, insiderName);
        }
        if (StringUtils.isNotBlank(reason)) {
            wrapper.like(InsiderInfo::getReason, reason);
        }
        if (StringUtils.isNotBlank(content)) {
            wrapper.like(InsiderInfo::getContent, content);
        }
        if (StringUtils.isNotBlank(status)) {
            wrapper.eq(InsiderInfo::getStatus, status);
        }

        wrapper.orderByDesc(InsiderInfo::getCreateTime);
        return page(pageParam, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createInsider(InsiderInfo insiderInfo) {
        insiderInfo.setCreateTime(LocalDateTime.now());
        insiderInfo.setUpdateTime(LocalDateTime.now());
        if (StringUtils.isBlank(insiderInfo.getStatus())) {
            insiderInfo.setStatus("draft");
        }
        insiderInfo.setDeleted(0);
        return save(insiderInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateInsider(InsiderInfo insiderInfo) {
        insiderInfo.setUpdateTime(LocalDateTime.now());
        return updateById(insiderInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteInsider(Long id) {
        return removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long id, String status) {
        InsiderInfo insiderInfo = new InsiderInfo();
        insiderInfo.setId(id);
        insiderInfo.setStatus(status);
        insiderInfo.setUpdateTime(LocalDateTime.now());
        return updateById(insiderInfo);
    }
}
