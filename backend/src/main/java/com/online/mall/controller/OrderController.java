package com.online.mall.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.online.mall.common.Result;
import com.online.mall.dto.OrderCreateDTO;
import com.online.mall.dto.OrderQueryDTO;
import com.online.mall.service.OrderService;
import com.online.mall.vo.OrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 订单控制器
 */
@Slf4j
@RestController
@RequestMapping("/order")
@Validated
@Tag(name = "订单管理", description = "订单相关接口")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Operation(summary = "创建订单")
    @PostMapping("/create")
    public Result<OrderVO> createOrder(
            HttpServletRequest request,
            @Valid @RequestBody OrderCreateDTO createDTO) {
        Long userId = (Long) request.getAttribute("userId");
        OrderVO orderVO = orderService.createOrder(userId, createDTO);
        return Result.success(orderVO, "订单创建成功");
    }

    @Operation(summary = "获取订单详情")
    @GetMapping("/{orderId}")
    public Result<OrderVO> getOrderById(
            HttpServletRequest request,
            @Parameter(description = "订单ID") @PathVariable Long orderId) {
        Long userId = (Long) request.getAttribute("userId");
        OrderVO orderVO = orderService.getOrderById(userId, orderId);
        return Result.success(orderVO);
    }

    @Operation(summary = "根据订单编号获取订单详情")
    @GetMapping("/no/{orderNo}")
    public Result<OrderVO> getOrderByOrderNo(
            HttpServletRequest request,
            @Parameter(description = "订单编号") @PathVariable String orderNo) {
        Long userId = (Long) request.getAttribute("userId");
        OrderVO orderVO = orderService.getOrderByOrderNo(userId, orderNo);
        return Result.success(orderVO);
    }

    @Operation(summary = "获取用户订单列表")
    @GetMapping("/list")
    public Result<Page<OrderVO>> getUserOrderList(
            HttpServletRequest request,
            @Valid OrderQueryDTO queryDTO) {
        Long userId = (Long) request.getAttribute("userId");
        Page<OrderVO> orderPage = orderService.getUserOrderList(userId, queryDTO);
        return Result.success(orderPage);
    }

    @Operation(summary = "取消订单")
    @PutMapping("/cancel/{orderId}")
    public Result<Void> cancelOrder(
            HttpServletRequest request,
            @Parameter(description = "订单ID") @PathVariable Long orderId,
            @Parameter(description = "取消原因") @RequestParam(required = false) String reason) {
        Long userId = (Long) request.getAttribute("userId");
        orderService.cancelOrder(userId, orderId, reason);
        return Result.success(null, "订单已取消");
    }

    @Operation(summary = "确认收货")
    @PutMapping("/confirm/{orderId}")
    public Result<Void> confirmReceive(
            HttpServletRequest request,
            @Parameter(description = "订单ID") @PathVariable Long orderId) {
        Long userId = (Long) request.getAttribute("userId");
        orderService.confirmReceive(userId, orderId);
        return Result.success(null, "确认收货成功");
    }

    @Operation(summary = "删除订单")
    @DeleteMapping("/{orderId}")
    public Result<Void> deleteOrder(
            HttpServletRequest request,
            @Parameter(description = "订单ID") @PathVariable Long orderId) {
        Long userId = (Long) request.getAttribute("userId");
        orderService.deleteOrder(userId, orderId);
        return Result.success(null, "订单已删除");
    }

    @Operation(summary = "获取订单数量统计")
    @GetMapping("/statistics")
    public Result<OrderService.OrderStatisticsVO> getOrderStatistics(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        OrderService.OrderStatisticsVO statistics = orderService.getOrderStatistics(userId);
        return Result.success(statistics);
    }
}
