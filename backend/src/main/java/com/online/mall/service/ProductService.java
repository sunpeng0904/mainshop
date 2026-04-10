package com.online.mall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.online.mall.entity.Product;
import com.online.mall.dto.ProductQueryDTO;
import com.online.mall.vo.ProductVO;

import java.util.List;

/**
 * 商品服务接口
 */
public interface ProductService extends IService<Product> {
    
    /**
     * 创建商品
     */
    ProductVO createProduct(Product product);
    
    /**
     * 更新商品
     */
    ProductVO updateProduct(Long productId, Product product);
    
    /**
     * 删除商品
     */
    void deleteProduct(Long productId);
    
    /**
     * 上架商品
     */
    void publishProduct(Long productId);
    
    /**
     * 下架商品
     */
    void unpublishProduct(Long productId);
    
    /**
     * 根据ID获取商品详情
     */
    ProductVO getProductById(Long productId);
    
    /**
     * 分页查询商品
     */
    Page<ProductVO> getProductPage(ProductQueryDTO queryDTO);
    
    /**
     * 根据分类查询商品
     */
    List<ProductVO> getProductsByCategory(Long categoryId);
    
    /**
     * 搜索商品
     */
    List<ProductVO> searchProducts(String keyword);
    
    /**
     * 获取热门商品
     */
    List<ProductVO> getHotProducts(Integer limit);
    
    /**
     * 获取新品推荐
     */
    List<ProductVO> getNewProducts(Integer limit);
    
    /**
     * 减少商品库存
     */
    boolean reduceStock(Long productId, Integer quantity);
    
    /**
     * 增加商品库存
     */
    boolean increaseStock(Long productId, Integer quantity);
    
    /**
     * 批量查询商品
     */
    List<ProductVO> batchGetProducts(List<Long> productIds);

    /**
     * 批量上架商品
     */
    void batchPublish(List<Long> productIds);

    /**
     * 批量下架商品
     */
    void batchUnpublish(List<Long> productIds);

    /**
     * 批量删除商品
     */
    void batchDelete(List<Long> productIds);
}