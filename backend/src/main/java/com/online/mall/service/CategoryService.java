package com.online.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.online.mall.entity.Category;
import com.online.mall.vo.CategoryVO;

import java.util.List;

/**
 * 分类服务接口
 */
public interface CategoryService extends IService<Category> {

    /**
     * 获取所有分类（树形结构，只含启用）
     */
    List<CategoryVO> getCategoryTree();

    /**
     * 获取所有分类（树形结构，包含禁用）
     */
    List<CategoryVO> getAllCategoryTree();

    /**
     * 获取所有一级分类（只含启用）
     */
    List<CategoryVO> getTopCategories();

    /**
     * 获取所有一级分类（包含禁用）
     */
    List<CategoryVO> getAllTopCategories();

    /**
     * 获取子分类（只含启用）
     */
    List<CategoryVO> getChildrenCategories(Long parentId);

    /**
     * 获取子分类（包含禁用）
     */
    List<CategoryVO> getAllChildrenCategories(Long parentId);

    /**
     * 根据ID获取分类详情
     */
    CategoryVO getCategoryById(Long categoryId);

    /**
     * 创建分类
     */
    CategoryVO createCategory(Category category);

    /**
     * 更新分类
     */
    CategoryVO updateCategory(Long categoryId, Category category);

    /**
     * 删除分类
     */
    void deleteCategory(Long categoryId);

    /**
     * 更新分类状态
     */
    void updateStatus(Long categoryId, Integer status);
}
