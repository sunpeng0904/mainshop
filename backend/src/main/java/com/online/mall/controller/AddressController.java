package com.online.mall.controller;

import com.online.mall.common.Result;
import com.online.mall.dto.AddressDTO;
import com.online.mall.service.UserAddressService;
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
 * 地址控制器
 */
@Slf4j
@RestController
@RequestMapping("/address")
@Validated
@Tag(name = "地址管理", description = "收货地址相关接口")
public class AddressController {

    @Autowired
    private UserAddressService addressService;

    @Operation(summary = "获取用户地址列表")
    @GetMapping("/list")
    public Result<List<AddressVO>> getAddressList(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<AddressVO> addressList = addressService.getAddressList(userId);
        return Result.success(addressList);
    }

    @Operation(summary = "获取地址详情")
    @GetMapping("/{addressId}")
    public Result<AddressVO> getAddressById(
            HttpServletRequest request,
            @Parameter(description = "地址ID") @PathVariable Long addressId) {
        Long userId = (Long) request.getAttribute("userId");
        AddressVO addressVO = addressService.getAddressById(userId, addressId);
        return Result.success(addressVO);
    }

    @Operation(summary = "添加地址")
    @PostMapping("/add")
    public Result<AddressVO> addAddress(
            HttpServletRequest request,
            @Valid @RequestBody AddressDTO addressDTO) {
        Long userId = (Long) request.getAttribute("userId");
        AddressVO addressVO = addressService.addAddress(userId, addressDTO);
        return Result.success(addressVO, "添加成功");
    }

    @Operation(summary = "更新地址")
    @PutMapping("/update")
    public Result<AddressVO> updateAddress(
            HttpServletRequest request,
            @Valid @RequestBody AddressDTO addressDTO) {
        Long userId = (Long) request.getAttribute("userId");
        AddressVO addressVO = addressService.updateAddress(userId, addressDTO);
        return Result.success(addressVO, "更新成功");
    }

    @Operation(summary = "删除地址")
    @DeleteMapping("/{addressId}")
    public Result<Void> deleteAddress(
            HttpServletRequest request,
            @Parameter(description = "地址ID") @PathVariable Long addressId) {
        Long userId = (Long) request.getAttribute("userId");
        addressService.deleteAddress(userId, addressId);
        return Result.success(null, "删除成功");
    }

    @Operation(summary = "设置默认地址")
    @PutMapping("/default/{addressId}")
    public Result<Void> setDefaultAddress(
            HttpServletRequest request,
            @Parameter(description = "地址ID") @PathVariable Long addressId) {
        Long userId = (Long) request.getAttribute("userId");
        addressService.setDefaultAddress(userId, addressId);
        return Result.success(null, "设置成功");
    }

    @Operation(summary = "获取默认地址")
    @GetMapping("/default")
    public Result<AddressVO> getDefaultAddress(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        AddressVO addressVO = addressService.getDefaultAddress(userId);
        return Result.success(addressVO);
    }
}
