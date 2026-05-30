package com.online.mall.controller;

import com.online.mall.common.Result;
import com.online.mall.dto.AddressDTO;
import com.online.mall.service.UserAddrService;
import com.online.mall.vo.AddressVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * 地址控制器（符合ATTRC2E词根规范）
 */
@Slf4j
@RestController
@RequestMapping("/address")
@Validated
@Tag(name = "地址管理", description = "收货地址相关接口")
public class AddrController {

    @Autowired
    private UserAddrService addrService;

    @Operation(summary = "获取用户地址列表")
    @GetMapping("/list")
    public Result<List<AddressVO>> getAddrList(HttpServletRequest request) {
        String userId = String.valueOf(request.getAttribute("userId"));
        List<AddressVO> addrList = addrService.getAddrList(userId);
        return Result.success(addrList);
    }

    @Operation(summary = "获取地址详情")
    @GetMapping("/{addrId}")
    public Result<AddressVO> getAddrById(
            HttpServletRequest request,
            @Parameter(description = "地址标识") @PathVariable String addrId) {
        String userId = String.valueOf(request.getAttribute("userId"));
        AddressVO addrVO = addrService.getAddrById(userId, addrId);
        return Result.success(addrVO);
    }

    @Operation(summary = "添加地址")
    @PostMapping("/add")
    public Result<AddressVO> addAddr(
            HttpServletRequest request,
            @Valid @RequestBody AddressDTO addressDTO) {
        String userId = String.valueOf(request.getAttribute("userId"));
        AddressVO addrVO = addrService.addAddr(userId, addressDTO);
        return Result.success(addrVO, "添加成功");
    }

    @Operation(summary = "更新地址")
    @PutMapping("/update")
    public Result<AddressVO> updateAddr(
            HttpServletRequest request,
            @Valid @RequestBody AddressDTO addressDTO) {
        String userId = String.valueOf(request.getAttribute("userId"));
        AddressVO addrVO = addrService.updateAddr(userId, addressDTO);
        return Result.success(addrVO, "更新成功");
    }

    @Operation(summary = "删除地址")
    @DeleteMapping("/{addrId}")
    public Result<Void> deleteAddr(
            HttpServletRequest request,
            @Parameter(description = "地址标识") @PathVariable String addrId) {
        String userId = String.valueOf(request.getAttribute("userId"));
        addrService.deleteAddr(userId, addrId);
        return Result.success(null, "删除成功");
    }

    @Operation(summary = "设置默认地址")
    @PutMapping("/default/{addrId}")
    public Result<Void> setDftAddr(
            HttpServletRequest request,
            @Parameter(description = "地址标识") @PathVariable String addrId) {
        String userId = String.valueOf(request.getAttribute("userId"));
        addrService.setDftAddr(userId, addrId);
        return Result.success(null, "设置成功");
    }

    @Operation(summary = "获取默认地址")
    @GetMapping("/default")
    public Result<AddressVO> getDftAddr(HttpServletRequest request) {
        String userId = String.valueOf(request.getAttribute("userId"));
        AddressVO addrVO = addrService.getDftAddr(userId);
        return Result.success(addrVO);
    }
}
