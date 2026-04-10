package com.online.mall.controller;

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
 * 分类控制器
 */
@Slf4j
@RestController
@RequestMapping("/category")
@Validated
@Tag(name = "分类管理", description = "商品分类相关接口")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Operation(summary = "获取分类树")
    @GetMapping("/tree")
    public Result<List<CategoryVO>> getCategoryTree() {
        List<CategoryVO> tree = categoryService.getCategoryTree();
        return Result.success(tree);
    }

    @Operation(summary = "获取一级分类列表")
    @GetMapping("/top")
    public Result<List<CategoryVO>> getTopCategories() {
        List<CategoryVO> categories = categoryService.getTopCategories();
        return Result.success(categories);
    }

    @Operation(summary = "获取子分类列表")
    @GetMapping("/children/{parentId}")
    public Result<List<CategoryVO>> getChildrenCategories(
            @Parameter(description = "父分类ID") @PathVariable Long parentId) {
        List<CategoryVO> categories = categoryService.getChildrenCategories(parentId);
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
    @PostMapping("/create")
    public Result<CategoryVO> createCategory(@Valid @RequestBody Category category) {
        CategoryVO vo = categoryService.createCategory(category);
        return Result.success(vo, "创建成功");
    }

    @Operation(summary = "更新分类")
    @PutMapping("/update/{categoryId}")
    public Result<CategoryVO> updateCategory(
            @Parameter(description = "分类ID") @PathVariable Long categoryId,
            @Valid @RequestBody Category category) {
        CategoryVO vo = categoryService.updateCategory(categoryId, category);
        return Result.success(vo, "更新成功");
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/{categoryId}")
    public Result<Void> deleteCategory(
            @Parameter(description = "分类ID") @PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return Result.success(null, "删除成功");
    }
}
