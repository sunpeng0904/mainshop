package com.online.mall.controller;

import com.online.mall.common.Result;
import com.online.mall.service.RegionService;
import com.online.mall.vo.RegionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 区域控制器
 */
@Slf4j
@RestController
@RequestMapping("/region")
@Tag(name = "区域管理", description = "省市区查询接口")
public class RegionController {

    @Autowired
    private RegionService regionService;

    @GetMapping("/provinces")
    @Operation(summary = "获取所有省份", description = "获取全国所有省份列表")
    public Result<List<RegionVO>> getProvinces() {
        List<RegionVO> provinces = regionService.getProvinces();
        return Result.success(provinces);
    }

    @GetMapping("/cities/{provinceCode}")
    @Operation(summary = "获取城市列表", description = "根据省份编码获取城市列表")
    public Result<List<RegionVO>> getCities(
            @Parameter(description = "省份编码") @PathVariable String provinceCode) {
        List<RegionVO> cities = regionService.getCitiesByProvinceCode(provinceCode);
        return Result.success(cities);
    }

    @GetMapping("/districts/{cityCode}")
    @Operation(summary = "获取区县列表", description = "根据城市编码获取区县列表")
    public Result<List<RegionVO>> getDistricts(
            @Parameter(description = "城市编码") @PathVariable String cityCode) {
        List<RegionVO> districts = regionService.getDistrictsByCityCode(cityCode);
        return Result.success(districts);
    }
}
