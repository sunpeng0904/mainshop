package com.online.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.online.mall.entity.Cart;
import com.online.mall.vo.CartVO;

import java.util.List;

/**
 * 购物车服务接口
 */
public interface CartService extends IService<Cart> {
    
    /**
     * 添加商品到购物车
     */
    CartVO addToCart(Long userId, Long productId, Integer quantity);
    
    /**
     * 更新购物车商品数量
     */
    CartVO updateCartItem(Long userId, Long cartId, Integer quantity);
    
    /**
     * 删除购物车商品
     */
    void removeCartItem(Long userId, Long cartId);
    
    /**
     * 批量删除购物车商品
     */
    void batchRemoveCartItems(Long userId, List<Long> cartIds);
    
    /**
     * 清空购物车
     */
    void clearCart(Long userId);
    
    /**
     * 获取用户购物车列表
     */
    List<CartVO> getCartList(Long userId);
    
    /**
     * 获取购物车商品数量
     */
    Integer getCartCount(Long userId);
    
    /**
     * 选中/取消选中购物车商品
     */
    void selectCartItem(Long userId, Long cartId, Boolean selected);
    
    /**
     * 全选/取消全选购物车商品
     */
    void selectAllCartItems(Long userId, Boolean selected);
    
    /**
     * 获取选中的购物车商品
     */
    List<CartVO> getSelectedCartItems(Long userId);
    
    /**
     * 计算购物车总金额
     */
    Double calculateCartTotal(Long userId);
}