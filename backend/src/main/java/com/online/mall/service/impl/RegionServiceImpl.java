package com.online.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.online.mall.entity.Region;
import com.online.mall.mapper.RegionMapper;
import com.online.mall.service.RegionService;
import com.online.mall.vo.RegionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 区域服务实现
 */
@Slf4j
@Service
public class RegionServiceImpl extends ServiceImpl<RegionMapper, Region> implements RegionService {

    @Override
    public List<RegionVO> getProvinces() {
        List<Region> regions = list(new QueryWrapper<Region>()
                .eq("level", 1)
                .eq("status", 1)
                .orderByAsc("sort"));

        return convertToVOList(regions);
    }

    @Override
    public List<RegionVO> getCitiesByProvinceCode(String provinceCode) {
        List<Region> regions = list(new QueryWrapper<Region>()
                .eq("parent_code", provinceCode)
                .eq("level", 2)
                .eq("status", 1)
                .orderByAsc("sort"));

        return convertToVOList(regions);
    }

    @Override
    public List<RegionVO> getDistrictsByCityCode(String cityCode) {
        List<Region> regions = list(new QueryWrapper<Region>()
                .eq("parent_code", cityCode)
                .eq("level", 3)
                .eq("status", 1)
                .orderByAsc("sort"));

        return convertToVOList(regions);
    }

    @Override
    public String getRegionNameByCode(String regionCode) {
        if (regionCode == null || regionCode.isEmpty()) {
            return null;
        }
        Region region = getOne(new QueryWrapper<Region>()
                .eq("region_code", regionCode)
                .eq("status", 1));
        return region != null ? region.getRegionName() : null;
    }

    @Override
    public Map<String, String> getRegionNamesByCodes(List<String> regionCodes) {
        Map<String, String> result = new HashMap<>();
        if (regionCodes == null || regionCodes.isEmpty()) {
            return result;
        }

        List<Region> regions = list(new QueryWrapper<Region>()
                .in("region_code", regionCodes)
                .eq("status", 1));

        for (Region region : regions) {
            result.put(region.getRegionCode(), region.getRegionName());
        }
        return result;
    }

    /**
     * 转换为VO列表
     */
    private List<RegionVO> convertToVOList(List<Region> regions) {
        List<RegionVO> voList = new ArrayList<>();
        for (Region region : regions) {
            RegionVO vo = new RegionVO();
            BeanUtils.copyProperties(region, vo);
            voList.add(vo);
        }
        return voList;
    }
}
