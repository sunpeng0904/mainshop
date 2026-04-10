package com.online.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.online.mall.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品Mapper接口
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    
    /**
     * 根据分类ID查询商品
     */
    @Select("SELECT * FROM product WHERE category_id = #{categoryId} AND status = 1 ORDER BY create_time DESC")
    List<Product> selectByCategoryId(@Param("categoryId") Long categoryId);
    
    /**
     * 根据商品名称模糊查询
     */
    @Select("SELECT * FROM product WHERE name LIKE CONCAT('%', #{keyword}, '%') AND status = 1")
    List<Product> searchByName(@Param("keyword") String keyword);
    
    /**
     * 分页查询商品
     */
    @Select("SELECT * FROM product WHERE status = 1")
    IPage<Product> selectPageVo(Page<Product> page);
    
    /**
     * 查询热门商品
     */
    @Select("SELECT * FROM product WHERE status = 1 ORDER BY sales DESC LIMIT #{limit}")
    List<Product> selectHotProducts(@Param("limit") Integer limit);
    
    /**
     * 查询新品
     */
    @Select("SELECT * FROM product WHERE status = 1 ORDER BY create_time DESC LIMIT #{limit}")
    List<Product> selectNewProducts(@Param("limit") Integer limit);
    
    /**
     * 减少库存
     */
    @Update("UPDATE product SET stock = stock - #{quantity}, sales = sales + #{quantity} WHERE id = #{productId} AND stock >= #{quantity}")
    int reduceStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);
    
    /**
     * 增加库存
     */
    @Update("UPDATE product SET stock = stock + #{quantity} WHERE id = #{productId}")
    int increaseStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);
    
    /**
     * 根据价格范围查询商品
     */
    @Select("SELECT * FROM product WHERE price BETWEEN #{minPrice} AND #{maxPrice} AND status = 1")
    List<Product> selectByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);
}