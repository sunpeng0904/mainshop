package com.online.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.online.mall.common.BusinessException;
import com.online.mall.dto.ProductQueryDTO;
import com.online.mall.entity.Product;
import com.online.mall.mapper.ProductMapper;
import com.online.mall.service.ProductService;
import com.online.mall.vo.ProductVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 商品服务实现
 */
@Slf4j
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Override
    @Transactional
    public ProductVO createProduct(Product product) {
        log.info("创建商品: {}", product.getName());

        product.setStatus(1); // 默认上架
        product.setSales(0);
        save(product);

        return convertToVO(product);
    }

    @Override
    @Transactional
    public ProductVO updateProduct(Long productId, Product product) {
        log.info("更新商品: productId={}", productId);

        Product existProduct = getById(productId);
        if (existProduct == null) {
            throw new BusinessException("product.not.found");
        }

        product.setId(productId);
        updateById(product);

        return convertToVO(getById(productId));
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        log.info("删除商品: productId={}", productId);

        Product product = getById(productId);
        if (product == null) {
            throw new BusinessException("product.not.found");
        }

        removeById(productId);
    }

    @Override
    @Transactional
    public void publishProduct(Long productId) {
        log.info("上架商品: productId={}", productId);

        Product product = getById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        product.setStatus(1);
        updateById(product);
    }

    @Override
    @Transactional
    public void unpublishProduct(Long productId) {
        log.info("下架商品: productId={}", productId);

        Product product = getById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        product.setStatus(0);
        updateById(product);
    }

    @Override
    public ProductVO getProductById(Long productId) {
        log.info("根据ID获取商品详情: productId={}", productId);

        Product product = getById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        return convertToVO(product);
    }

    @Override
    public Page<ProductVO> getProductPage(ProductQueryDTO queryDTO) {
        log.info("分页查询商品: {}", queryDTO);

        Page<Product> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        QueryWrapper<Product> wrapper = new QueryWrapper<Product>()
                .eq(queryDTO.getStatus() != null, "status", queryDTO.getStatus())
                .eq(queryDTO.getCategoryId() != null, "category_id", queryDTO.getCategoryId())
                .like(queryDTO.getName() != null && !queryDTO.getName().isEmpty(), "name", queryDTO.getName())
                .ge(queryDTO.getMinPrice() != null, "price", queryDTO.getMinPrice())
                .le(queryDTO.getMaxPrice() != null, "price", queryDTO.getMaxPrice());

        // 排序
        if ("price".equals(queryDTO.getSortField())) {
            wrapper.orderBy(true, "asc".equals(queryDTO.getSortOrder()), "price");
        } else if ("sales".equals(queryDTO.getSortField())) {
            wrapper.orderByDesc("sales");
        } else {
            wrapper.orderByDesc("create_time");
        }

        Page<Product> productPage = page(page, wrapper);

        Page<ProductVO> voPage = new Page<>(productPage.getCurrent(), productPage.getSize(), productPage.getTotal());
        List<ProductVO> voList = new ArrayList<>();

        for (Product product : productPage.getRecords()) {
            voList.add(convertToVO(product));
        }

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public List<ProductVO> getProductsByCategory(Long categoryId) {
        log.info("根据分类查询商品: categoryId={}", categoryId);

        List<Product> products = list(new QueryWrapper<Product>()
                .eq("category_id", categoryId)
                .eq("status", 1)
                .orderByDesc("create_time"));

        return convertToVOList(products);
    }

    @Override
    public List<ProductVO> searchProducts(String keyword) {
        log.info("搜索商品: keyword={}", keyword);

        List<Product> products = list(new QueryWrapper<Product>()
                .eq("status", 1)
                .and(wrapper -> wrapper
                        .like("name", keyword)
                        .or()
                        .like("description", keyword))
                .orderByDesc("sales")
                .last("LIMIT 20"));

        return convertToVOList(products);
    }

    @Override
    public List<ProductVO> getHotProducts(Integer limit) {
        log.info("获取热门商品: limit={}", limit);

        List<Product> products = list(new QueryWrapper<Product>()
                .eq("status", 1)
                .orderByDesc("sales")
                .last("LIMIT " + limit));

        return convertToVOList(products);
    }

    @Override
    public List<ProductVO> getNewProducts(Integer limit) {
        log.info("获取新品推荐: limit={}", limit);

        List<Product> products = list(new QueryWrapper<Product>()
                .eq("status", 1)
                .orderByDesc("create_time")
                .last("LIMIT " + limit));

        return convertToVOList(products);
    }

    @Override
    @Transactional
    public boolean reduceStock(Long productId, Integer quantity) {
        log.info("减少商品库存: productId={}, quantity={}", productId, quantity);

        Product product = getById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        if (product.getStock() < quantity) {
            throw new BusinessException("product.stock.insufficient");
        }

        product.setStock(product.getStock() - quantity);
        product.setSales(product.getSales() + quantity);
        return updateById(product);
    }

    @Override
    @Transactional
    public boolean increaseStock(Long productId, Integer quantity) {
        log.info("增加商品库存: productId={}, quantity={}", productId, quantity);

        Product product = getById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        product.setStock(product.getStock() + quantity);
        product.setSales(Math.max(0, product.getSales() - quantity));
        return updateById(product);
    }

    @Override
    public List<ProductVO> batchGetProducts(List<Long> productIds) {
        log.info("批量查询商品: productIds={}", productIds);

        if (productIds == null || productIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Product> products = list(new QueryWrapper<Product>()
                .in("id", productIds)
                .eq("status", 1));

        return convertToVOList(products);
    }

    /**
     * 转换为VO
     */
    private ProductVO convertToVO(Product product) {
        ProductVO vo = new ProductVO();
        BeanUtils.copyProperties(product, vo);

        // 处理图片列表
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                List<String> imageList = mapper.readValue(product.getImages(), new TypeReference<List<String>>() {});
                vo.setImages(imageList);
            } catch (Exception e) {
                // 如果解析失败，作为单个URL处理
                vo.setImages(Collections.singletonList(product.getImages()));
            }
        } else {
            vo.setImages(new ArrayList<>());
        }

        return vo;
    }

    /**
     * 批量转换为VO列表
     */
    private List<ProductVO> convertToVOList(List<Product> products) {
        List<ProductVO> voList = new ArrayList<>();
        for (Product product : products) {
            voList.add(convertToVO(product));
        }
        return voList;
    }
}
