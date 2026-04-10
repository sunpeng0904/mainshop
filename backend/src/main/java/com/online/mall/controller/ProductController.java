package com.online.mall.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.online.mall.common.Result;
import com.online.mall.dto.ProductQueryDTO;
import com.online.mall.entity.Product;
import com.online.mall.service.ProductService;
import com.online.mall.vo.ProductVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 商品控制器
 */
@Slf4j
@RestController
@RequestMapping("/product")
@Validated
@Tag(name = "商品管理", description = "商品相关接口")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Operation(summary = "分页查询商品列表")
    @GetMapping("/list")
    public Result<Page<ProductVO>> getProductList(@Valid ProductQueryDTO queryDTO) {
        Page<ProductVO> page = productService.getProductPage(queryDTO);
        return Result.success(page);
    }

    @Operation(summary = "获取商品详情")
    @GetMapping("/{productId}")
    public Result<ProductVO> getProductById(
            @Parameter(description = "商品ID") @PathVariable Long productId) {
        ProductVO productVO = productService.getProductById(productId);
        return Result.success(productVO);
    }

    @Operation(summary = "根据分类查询商品")
    @GetMapping("/category/{categoryId}")
    public Result<List<ProductVO>> getProductsByCategory(
            @Parameter(description = "分类ID") @PathVariable Long categoryId) {
        List<ProductVO> products = productService.getProductsByCategory(categoryId);
        return Result.success(products);
    }

    @Operation(summary = "搜索商品")
    @GetMapping("/search")
    public Result<List<ProductVO>> searchProducts(
            @Parameter(description = "搜索关键词") @RequestParam String keyword) {
        List<ProductVO> products = productService.searchProducts(keyword);
        return Result.success(products);
    }

    @Operation(summary = "获取热门商品")
    @GetMapping("/hot")
    public Result<List<ProductVO>> getHotProducts(
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "10") Integer limit) {
        List<ProductVO> products = productService.getHotProducts(limit);
        return Result.success(products);
    }

    @Operation(summary = "获取新品推荐")
    @GetMapping("/new")
    public Result<List<ProductVO>> getNewProducts(
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "10") Integer limit) {
        List<ProductVO> products = productService.getNewProducts(limit);
        return Result.success(products);
    }

    @Operation(summary = "批量查询商品")
    @PostMapping("/batch")
    public Result<List<ProductVO>> batchGetProducts(@RequestBody List<Long> productIds) {
        List<ProductVO> products = productService.batchGetProducts(productIds);
        return Result.success(products);
    }

    @Operation(summary = "服务健康检查")
    @GetMapping("/health")
    public Result<String> healthCheck() {
        return Result.success("Product service is healthy");
    }

    // ============= 管理端接口 =============

    @Operation(summary = "创建商品")
    @PostMapping("/create")
    public Result<ProductVO> createProduct(@Valid @RequestBody Product product) {
        ProductVO productVO = productService.createProduct(product);
        return Result.success(productVO, "创建成功");
    }

    @Operation(summary = "更新商品")
    @PutMapping("/update/{productId}")
    public Result<ProductVO> updateProduct(
            @Parameter(description = "商品ID") @PathVariable Long productId,
            @Valid @RequestBody Product product) {
        ProductVO productVO = productService.updateProduct(productId, product);
        return Result.success(productVO, "更新成功");
    }

    @Operation(summary = "删除商品")
    @DeleteMapping("/{productId}")
    public Result<Void> deleteProduct(
            @Parameter(description = "商品ID") @PathVariable Long productId) {
        productService.deleteProduct(productId);
        return Result.success(null, "删除成功");
    }

    @Operation(summary = "上架商品")
    @PutMapping("/publish/{productId}")
    public Result<Void> publishProduct(
            @Parameter(description = "商品ID") @PathVariable Long productId) {
        productService.publishProduct(productId);
        return Result.success(null, "上架成功");
    }

    @Operation(summary = "下架商品")
    @PutMapping("/unpublish/{productId}")
    public Result<Void> unpublishProduct(
            @Parameter(description = "商品ID") @PathVariable Long productId) {
        productService.unpublishProduct(productId);
        return Result.success(null, "下架成功");
    }
}
