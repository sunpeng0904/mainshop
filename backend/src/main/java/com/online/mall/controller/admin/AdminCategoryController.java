package com.online.mall.controller.admin;

import com.online.mall.common.Result;
import com.online.mall.entity.Category;
import com.online.mall.service.CategoryService;
import com.online.mall.vo.CategoryVO;
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
 * 管理员分类控制器
 */
@Slf4j
@RestController
@RequestMapping("/admin/category")
@Validated
@Tag(name = "管理员-分类管理", description = "管理员分类管理接口")
public class AdminCategoryController {

    @Autowired
    private CategoryService categoryService;

    @Operation(summary = "获取分类树（包含禁用）")
    @GetMapping("/tree")
    public Result<List<CategoryVO>> getCategoryTree() {
        List<CategoryVO> tree = categoryService.getAllCategoryTree();
        return Result.success(tree);
    }

    @Operation(summary = "获取所有一级分类")
    @GetMapping("/top")
    public Result<List<CategoryVO>> getTopCategories() {
        List<CategoryVO> categories = categoryService.getAllTopCategories();
        return Result.success(categories);
    }

    @Operation(summary = "获取子分类列表")
    @GetMapping("/children/{parentId}")
    public Result<List<CategoryVO>> getChildrenCategories(
            @Parameter(description = "父分类ID") @PathVariable Long parentId) {
        List<CategoryVO> categories = categoryService.getAllChildrenCategories(parentId);
        return Result.success(categories);
    }

    @Operation(summary = "获取分类详情")
    @GetMapping("/{categoryId}")
    public Result<CategoryVO> getCategoryById(
            @Parameter(description = "分类ID") @PathVariable Long categoryId) {
        CategoryVO category = categoryService.getCategoryById(categoryId);
        return Result.success(category);
    }

    @Operation(summary = "创建分类")
    @PostMapping
    public Result<CategoryVO> createCategory(@Valid @RequestBody Category category) {
        log.info("管理员创建分类: {}", category.getName());
        // 设置默认值
        if (category.getStatus() == null) {
            category.setStatus(1);
        }
        if (category.getSort() == null) {
            category.setSort(0);
        }
        if (category.getLevel() == null) {
            if (category.getParentId() == null || category.getParentId() == 0) {
                category.setParentId(0L);
                category.setLevel(1);
            } else {
                Category parent = categoryService.getById(category.getParentId());
                if (parent != null) {
                    category.setLevel(parent.getLevel() + 1);
                }
            }
        }
        CategoryVO vo = categoryService.createCategory(category);
        return Result.success(vo, "分类创建成功");
    }

    @Operation(summary = "更新分类")
    @PutMapping("/{categoryId}")
    public Result<CategoryVO> updateCategory(
            @Parameter(description = "分类ID") @PathVariable Long categoryId,
            @Valid @RequestBody Category category) {
        log.info("管理员更新分类: categoryId={}", categoryId);
        CategoryVO vo = categoryService.updateCategory(categoryId, category);
        return Result.success(vo, "分类更新成功");
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/{categoryId}")
    public Result<Void> deleteCategory(
            @Parameter(description = "分类ID") @PathVariable Long categoryId) {
        log.info("管理员删除分类: categoryId={}", categoryId);
        categoryService.deleteCategory(categoryId);
        return Result.success(null, "分类删除成功");
    }

    @Operation(summary = "启用分类")
    @PutMapping("/{categoryId}/enable")
    public Result<Void> enableCategory(
            @Parameter(description = "分类ID") @PathVariable Long categoryId) {
        log.info("管理员启用分类: categoryId={}", categoryId);
        categoryService.updateStatus(categoryId, 1);
        return Result.success(null, "分类已启用");
    }

    @Operation(summary = "禁用分类")
    @PutMapping("/{categoryId}/disable")
    public Result<Void> disableCategory(
            @Parameter(description = "分类ID") @PathVariable Long categoryId) {
        log.info("管理员禁用分类: categoryId={}", categoryId);
        categoryService.updateStatus(categoryId, 0);
        return Result.success(null, "分类已禁用");
    }
}
