package com.online.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.online.mall.entity.Region;
import com.online.mall.vo.RegionVO;

import java.util.List;

/**
 * 区域服务接口
 */
public interface RegionService extends IService<Region> {

    /**
     * 获取所有省份
     */
    List<RegionVO> getProvinces();

    /**
     * 根据省份编码获取城市列表
     */
    List<RegionVO> getCitiesByProvinceCode(String provinceCode);

    /**
     * 根据城市编码获取区县列表
     */
    List<RegionVO> getDistrictsByCityCode(String cityCode);

    /**
     * 根据区域编码获取区域名称
     */
    String getRegionNameByCode(String regionCode);

    /**
     * 批量获取区域名称
     */
    java.util.Map<String, String> getRegionNamesByCodes(List<String> regionCodes);
}
