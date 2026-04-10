package com.online.mall.controller.admin;

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
 * 管理员商品控制器
 * 所有接口路径前缀为 /admin/product
 * 由SecurityConfig配置需要ADMIN角色才能访问
 */
@Slf4j
@RestController
@RequestMapping("/admin/product")
@Validated
@Tag(name = "管理员-商品管理", description = "管理员商品管理接口")
public class AdminProductController {

    @Autowired
    private ProductService productService;

    @Operation(summary = "分页查询商品列表(管理端)")
    @GetMapping("/list")
    public Result<Page<ProductVO>> getProductList(@Valid ProductQueryDTO queryDTO) {
        Page<ProductVO> page = productService.getProductPage(queryDTO);
        return Result.success(page);
    }

    @Operation(summary = "获取商品详情(管理端)")
    @GetMapping("/{productId}")
    public Result<ProductVO> getProductById(
            @Parameter(description = "商品ID") @PathVariable Long productId) {
        ProductVO productVO = productService.getProductById(productId);
        return Result.success(productVO);
    }

    @Operation(summary = "创建商品")
    @PostMapping
    public Result<ProductVO> createProduct(@Valid @RequestBody Product product) {
        log.info("管理员创建商品: {}", product.getName());
        ProductVO productVO = productService.createProduct(product);
        return Result.success(productVO, "product.create.success");
    }

    @Operation(summary = "更新商品")
    @PutMapping("/{productId}")
    public Result<ProductVO> updateProduct(
            @Parameter(description = "商品ID") @PathVariable Long productId,
            @Valid @RequestBody Product product) {
        log.info("管理员更新商品: productId={}", productId);
        ProductVO productVO = productService.updateProduct(productId, product);
        return Result.success(productVO, "product.update.success");
    }

    @Operation(summary = "删除商品")
    @DeleteMapping("/{productId}")
    public Result<Void> deleteProduct(
            @Parameter(description = "商品ID") @PathVariable Long productId) {
        log.info("管理员删除商品: productId={}", productId);
        productService.deleteProduct(productId);
        return Result.success(null, "product.delete.success");
    }

    @Operation(summary = "上架商品")
    @PutMapping("/{productId}/publish")
    public Result<Void> publishProduct(
            @Parameter(description = "商品ID") @PathVariable Long productId) {
        log.info("管理员上架商品: productId={}", productId);
        productService.publishProduct(productId);
        return Result.success(null, "product.publish.success");
    }

    @Operation(summary = "下架商品")
    @PutMapping("/{productId}/unpublish")
    public Result<Void> unpublishProduct(
            @Parameter(description = "商品ID") @PathVariable Long productId) {
        log.info("管理员下架商品: productId={}", productId);
        productService.unpublishProduct(productId);
        return Result.success(null, "product.unpublish.success");
    }

    @Operation(summary = "批量上架")
    @PutMapping("/batch/publish")
    public Result<Void> batchPublish(@RequestBody List<Long> productIds) {
        log.info("管理员批量上架商品: productIds={}", productIds);
        productService.batchPublish(productIds);
        return Result.success(null, "product.batch.publish.success");
    }

    @Operation(summary = "批量下架")
    @PutMapping("/batch/unpublish")
    public Result<Void> batchUnpublish(@RequestBody List<Long> productIds) {
        log.info("管理员批量下架商品: productIds={}", productIds);
        productService.batchUnpublish(productIds);
        return Result.success(null, "product.batch.unpublish.success");
    }

    @Operation(summary = "批量删除")
    @DeleteMapping("/batch")
    public Result<Void> batchDelete(@RequestBody List<Long> productIds) {
        log.info("管理员批量删除商品: productIds={}", productIds);
        productService.batchDelete(productIds);
        return Result.success(null, "product.batch.delete.success");
    }
}
