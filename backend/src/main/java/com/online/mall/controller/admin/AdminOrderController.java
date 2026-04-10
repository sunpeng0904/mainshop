package com.online.mall.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.online.mall.common.Result;
import com.online.mall.dto.OrderQueryDTO;
import com.online.mall.service.OrderService;
import com.online.mall.vo.OrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 管理员订单控制器
 */
@Slf4j
@RestController
@RequestMapping("/admin/order")
@Tag(name = "管理员-订单管理", description = "管理员订单相关接口")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    @Operation(summary = "获取订单列表")
    @GetMapping("/list")
    public Result<Page<OrderVO>> getOrderList(@Valid OrderQueryDTO queryDTO) {
        Page<OrderVO> orderPage = orderService.getAdminOrderList(queryDTO);
        return Result.success(orderPage);
    }

    @Operation(summary = "获取订单详情")
    @GetMapping("/{orderId}")
    public Result<OrderVO> getOrderById(
            @Parameter(description = "订单ID") @PathVariable Long orderId) {
        OrderVO orderVO = orderService.getAdminOrderById(orderId);
        return Result.success(orderVO);
    }

    @Operation(summary = "发货")
    @PutMapping("/ship/{orderId}")
    public Result<Void> shipOrder(
            @Parameter(description = "订单ID") @PathVariable Long orderId,
            @Parameter(description = "物流公司") @RequestParam(required = false) String logisticsCompany,
            @Parameter(description = "物流单号") @RequestParam(required = false) String logisticsNo) {
        orderService.shipOrder(orderId, logisticsCompany, logisticsNo);
        return Result.success(null, "发货成功");
    }

    @Operation(summary = "取消订单")
    @PutMapping("/cancel/{orderId}")
    public Result<Void> cancelOrder(
            @Parameter(description = "订单ID") @PathVariable Long orderId,
            @Parameter(description = "取消原因") @RequestParam(required = false) String reason) {
        orderService.adminCancelOrder(orderId, reason);
        return Result.success(null, "订单已取消");
    }

    @Operation(summary = "删除订单")
    @DeleteMapping("/{orderId}")
    public Result<Void> deleteOrder(
            @Parameter(description = "订单ID") @PathVariable Long orderId) {
        orderService.adminDeleteOrder(orderId);
        return Result.success(null, "订单已删除");
    }

    @Operation(summary = "获取订单统计")
    @GetMapping("/statistics")
    public Result<OrderService.OrderStatisticsVO> getOrderStatistics() {
        OrderService.OrderStatisticsVO statistics = orderService.getAdminOrderStatistics();
        return Result.success(statistics);
    }
}
