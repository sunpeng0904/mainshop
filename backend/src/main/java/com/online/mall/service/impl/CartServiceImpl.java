package com.online.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.online.mall.common.BusinessException;
import com.online.mall.entity.Cart;
import com.online.mall.entity.Product;
import com.online.mall.mapper.CartMapper;
import com.online.mall.service.CartService;
import com.online.mall.service.ProductService;
import com.online.mall.vo.CartVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 购物车服务实现
 */
@Slf4j
@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    @Autowired
    private ProductService productService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public CartVO addToCart(Long userId, Long productId, Integer quantity) {
        log.info("添加购物车: userId={}, productId={}, quantity={}", userId, productId, quantity);

        // 查询商品信息
        Product product = productService.getById(productId);
        if (product == null) {
            throw new BusinessException("product.not.found");
        }
        if (product.getStatus() != 1) {
            throw new BusinessException("product.offline");
        }
        if (product.getStock() < quantity) {
            throw new BusinessException("product.stock.insufficient");
        }

        // 查询购物车是否已存在该商品
        Cart cart = getOne(new QueryWrapper<Cart>()
                .eq("user_id", userId)
                .eq("product_id", productId));

        if (cart != null) {
            // 更新数量
            int newQuantity = cart.getQuantity() + quantity;
            if (newQuantity > product.getStock()) {
                throw new BusinessException("商品库存不足");
            }
            cart.setQuantity(newQuantity);
            updateById(cart);
        } else {
            // 新增购物车记录
            cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setQuantity(quantity);
            cart.setSelected(1);
            save(cart);
        }

        return convertToVO(cart, product);
    }

    @Override
    @Transactional
    public CartVO updateCartItem(Long userId, Long cartId, Integer quantity) {
        log.info("更新购物车商品数量: userId={}, cartId={}, quantity={}", userId, cartId, quantity);

        Cart cart = getById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new BusinessException("cart.not.found");
        }

        if (quantity <= 0) {
            // 数量为0时删除
            removeById(cartId);
            return null;
        }

        Product product = productService.getById(cart.getProductId());
        if (product == null) {
            throw new BusinessException("product.not.found");
        }
        if (quantity > product.getStock()) {
            throw new BusinessException("product.stock.insufficient");
        }

        cart.setQuantity(quantity);
        updateById(cart);

        return convertToVO(cart, product);
    }

    @Override
    @Transactional
    public void removeCartItem(Long userId, Long cartId) {
        log.info("删除购物车商品: userId={}, cartId={}", userId, cartId);

        Cart cart = getById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new BusinessException("cart.not.found");
        }

        removeById(cartId);
    }

    @Override
    @Transactional
    public void batchRemoveCartItems(Long userId, List<Long> cartIds) {
        log.info("批量删除购物车商品: userId={}, cartIds={}", userId, cartIds);

        remove(new QueryWrapper<Cart>()
                .eq("user_id", userId)
                .in("id", cartIds));
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        log.info("清空购物车: userId={}", userId);

        remove(new QueryWrapper<Cart>().eq("user_id", userId));
    }

    @Override
    public List<CartVO> getCartList(Long userId) {
        log.info("获取用户购物车列表: userId={}", userId);

        List<Cart> carts = list(new QueryWrapper<Cart>()
                .eq("user_id", userId)
                .orderByDesc("create_time"));

        List<CartVO> cartVOList = new ArrayList<>();
        for (Cart cart : carts) {
            Product product = productService.getById(cart.getProductId());
            if (product != null) {
                cartVOList.add(convertToVO(cart, product));
            }
        }

        return cartVOList;
    }

    @Override
    public Integer getCartCount(Long userId) {
        log.info("获取购物车商品数量: userId={}", userId);

        Long count = count(new QueryWrapper<Cart>().eq("user_id", userId));
        return count.intValue();
    }

    @Override
    @Transactional
    public void selectCartItem(Long userId, Long cartId, Boolean selected) {
        log.info("选中/取消选中购物车商品: userId={}, cartId={}, selected={}", userId, cartId, selected);

        Cart cart = getById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new BusinessException("cart.not.found");
        }

        cart.setSelected(selected ? 1 : 0);
        updateById(cart);
    }

    @Override
    @Transactional
    public void selectAllCartItems(Long userId, Boolean selected) {
        log.info("全选/取消全选购物车商品: userId={}, selected={}", userId, selected);

        Cart updateCart = new Cart();
        updateCart.setSelected(selected ? 1 : 0);
        update(updateCart, new QueryWrapper<Cart>().eq("user_id", userId));
    }

    @Override
    public List<CartVO> getSelectedCartItems(Long userId) {
        log.info("获取选中的购物车商品: userId={}", userId);

        List<Cart> carts = list(new QueryWrapper<Cart>()
                .eq("user_id", userId)
                .eq("selected", 1));

        List<CartVO> cartVOList = new ArrayList<>();
        for (Cart cart : carts) {
            Product product = productService.getById(cart.getProductId());
            if (product != null && product.getStatus() == 1) {
                cartVOList.add(convertToVO(cart, product));
            }
        }

        return cartVOList;
    }

    @Override
    public Double calculateCartTotal(Long userId) {
        log.info("计算购物车总金额: userId={}", userId);

        List<CartVO> selectedItems = getSelectedCartItems(userId);
        BigDecimal total = BigDecimal.ZERO;

        for (CartVO item : selectedItems) {
            if (item.getSubtotal() != null) {
                total = total.add(item.getSubtotal());
            }
        }

        return total.doubleValue();
    }

    /**
     * 转换为VO
     */
    private CartVO convertToVO(Cart cart, Product product) {
        CartVO vo = new CartVO();
        vo.setId(cart.getId());
        vo.setUserId(cart.getUserId());
        vo.setProductId(cart.getProductId());
        vo.setQuantity(cart.getQuantity());
        vo.setSelected(cart.getSelected());
        vo.setCreateTime(cart.getCreateTime());
        vo.setUpdateTime(cart.getUpdateTime());

        // 商品信息
        vo.setProductName(product.getName());
        vo.setProductPrice(product.getPrice());
        vo.setProductStock(product.getStock());
        vo.setProductStatus(product.getStatus());

        // 处理图片 - 从商品图片JSON数组中取第一张
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            String images = product.getImages().trim();
            try {
                if (images.startsWith("[")) {
                    // JSON数组格式，解析并取第一张
                    List<String> imageList = objectMapper.readValue(images, new TypeReference<List<String>>() {});
                    if (imageList != null && !imageList.isEmpty()) {
                        vo.setProductImage(imageList.get(0));
                    }
                } else {
                    // 单个图片路径
                    vo.setProductImage(images);
                }
            } catch (Exception e) {
                log.warn("解析商品图片失败: productId={}, images={}", product.getId(), images);
                // 尝试简单处理
                vo.setProductImage(images.replace("[", "").replace("]", "").replace("\"", "").split(",")[0].trim());
            }
        }

        // 计算小计
        if (product.getPrice() != null && cart.getQuantity() != null) {
            vo.setSubtotal(product.getPrice().multiply(new BigDecimal(cart.getQuantity())));
        }

        return vo;
    }
}
