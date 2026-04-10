package com.online.mall.controller;

import com.online.mall.common.Result;
import com.online.mall.service.CartService;
import com.online.mall.vo.CartVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 购物车控制器
 */
@Slf4j
@RestController
@RequestMapping("/cart")
@Validated
@Tag(name = "购物车管理", description = "购物车相关接口")
public class CartController {

    @Autowired
    private CartService cartService;

    @Operation(summary = "添加商品到购物车")
    @PostMapping("/add")
    public Result<CartVO> addToCart(
            HttpServletRequest request,
            @Parameter(description = "商品ID") @RequestParam Long productId,
            @Parameter(description = "数量") @RequestParam(defaultValue = "1") Integer quantity) {
        Long userId = (Long) request.getAttribute("userId");
        CartVO cartVO = cartService.addToCart(userId, productId, quantity);
        return Result.success(cartVO, "添加成功");
    }

    @Operation(summary = "更新购物车商品数量")
    @PutMapping("/update")
    public Result<CartVO> updateCartItem(
            HttpServletRequest request,
            @Parameter(description = "购物车ID") @RequestParam Long cartId,
            @Parameter(description = "数量") @RequestParam Integer quantity) {
        Long userId = (Long) request.getAttribute("userId");
        CartVO cartVO = cartService.updateCartItem(userId, cartId, quantity);
        return Result.success(cartVO, "更新成功");
    }

    @Operation(summary = "删除购物车商品")
    @DeleteMapping("/{cartId}")
    public Result<Void> removeCartItem(
            HttpServletRequest request,
            @Parameter(description = "购物车ID") @PathVariable Long cartId) {
        Long userId = (Long) request.getAttribute("userId");
        cartService.removeCartItem(userId, cartId);
        return Result.success(null, "删除成功");
    }

    @Operation(summary = "批量删除购物车商品")
    @DeleteMapping("/batch")
    public Result<Void> batchRemoveCartItems(
            HttpServletRequest request,
            @RequestBody List<Long> cartIds) {
        Long userId = (Long) request.getAttribute("userId");
        cartService.batchRemoveCartItems(userId, cartIds);
        return Result.success(null, "删除成功");
    }

    @Operation(summary = "清空购物车")
    @DeleteMapping("/clear")
    public Result<Void> clearCart(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        cartService.clearCart(userId);
        return Result.success(null, "清空成功");
    }

    @Operation(summary = "获取购物车列表")
    @GetMapping("/list")
    public Result<List<CartVO>> getCartList(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<CartVO> cartList = cartService.getCartList(userId);
        return Result.success(cartList);
    }

    @Operation(summary = "获取购物车商品数量")
    @GetMapping("/count")
    public Result<Integer> getCartCount(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Integer count = cartService.getCartCount(userId);
        return Result.success(count);
    }

    @Operation(summary = "选中/取消选中购物车商品")
    @PutMapping("/select")
    public Result<Void> selectCartItem(
            HttpServletRequest request,
            @Parameter(description = "购物车ID") @RequestParam Long cartId,
            @Parameter(description = "是否选中") @RequestParam Boolean selected) {
        Long userId = (Long) request.getAttribute("userId");
        cartService.selectCartItem(userId, cartId, selected);
        return Result.success(null, "操作成功");
    }

    @Operation(summary = "全选/取消全选购物车商品")
    @PutMapping("/select-all")
    public Result<Void> selectAllCartItems(
            HttpServletRequest request,
            @Parameter(description = "是否全选") @RequestParam Boolean selected) {
        Long userId = (Long) request.getAttribute("userId");
        cartService.selectAllCartItems(userId, selected);
        return Result.success(null, "操作成功");
    }

    @Operation(summary = "获取选中的购物车商品")
    @GetMapping("/selected")
    public Result<List<CartVO>> getSelectedCartItems(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<CartVO> selectedItems = cartService.getSelectedCartItems(userId);
        return Result.success(selectedItems);
    }

    @Operation(summary = "计算购物车总金额")
    @GetMapping("/total")
    public Result<Double> calculateCartTotal(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Double total = cartService.calculateCartTotal(userId);
        return Result.success(total);
    }
}
