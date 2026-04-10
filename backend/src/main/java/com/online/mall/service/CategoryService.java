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
     * 获取所有分类（树形结构）
     */
    List<CategoryVO> getCategoryTree();

    /**
     * 获取所有一级分类
     */
    List<CategoryVO> getTopCategories();

    /**
     * 获取子分类
     */
    List<CategoryVO> getChildrenCategories(Long parentId);

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
}
