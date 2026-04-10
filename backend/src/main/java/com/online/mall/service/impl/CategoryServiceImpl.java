package com.online.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.online.mall.common.BusinessException;
import com.online.mall.entity.Category;
import com.online.mall.mapper.CategoryMapper;
import com.online.mall.service.CategoryService;
import com.online.mall.vo.CategoryVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 分类服务实现
 */
@Slf4j
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public List<CategoryVO> getCategoryTree() {
        // 获取所有启用的分类
        List<Category> allCategories = this.list(
            new LambdaQueryWrapper<Category>()
                .eq(Category::getStatus, 1)
                .orderByAsc(Category::getSort)
        );

        // 转换为VO
        List<CategoryVO> allVOs = allCategories.stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());

        // 构建树形结构
        return buildTree(allVOs, 0L);
    }

    @Override
    public List<CategoryVO> getTopCategories() {
        List<Category> categories = this.list(
            new LambdaQueryWrapper<Category>()
                .eq(Category::getParentId, 0)
                .eq(Category::getStatus, 1)
                .orderByAsc(Category::getSort)
        );

        return categories.stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
    }

    @Override
    public List<CategoryVO> getChildrenCategories(Long parentId) {
        List<Category> categories = this.list(
            new LambdaQueryWrapper<Category>()
                .eq(Category::getParentId, parentId)
                .eq(Category::getStatus, 1)
                .orderByAsc(Category::getSort)
        );

        return categories.stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
    }

    @Override
    public CategoryVO getCategoryById(Long categoryId) {
        Category category = this.getById(categoryId);
        if (category == null) {
            return null;
        }
        CategoryVO vo = convertToVO(category);
        // 加载子分类
        vo.setChildren(getChildrenCategories(categoryId));
        return vo;
    }

    @Override
    public CategoryVO createCategory(Category category) {
        this.save(category);
        return convertToVO(category);
    }

    @Override
    public CategoryVO updateCategory(Long categoryId, Category category) {
        category.setId(categoryId);
        this.updateById(category);
        return convertToVO(category);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        // 检查是否有子分类
        long count = this.count(
            new LambdaQueryWrapper<Category>()
                .eq(Category::getParentId, categoryId)
        );
        if (count > 0) {
            throw new BusinessException("category.has.children");
        }
        this.removeById(categoryId);
    }

    /**
     * 转换为VO
     */
    private CategoryVO convertToVO(Category category) {
        CategoryVO vo = new CategoryVO();
        BeanUtils.copyProperties(category, vo);
        return vo;
    }

    /**
     * 构建树形结构
     */
    private List<CategoryVO> buildTree(List<CategoryVO> allVOs, Long parentId) {
        return allVOs.stream()
            .filter(vo -> parentId.equals(vo.getParentId()))
            .peek(vo -> vo.setChildren(buildTree(allVOs, vo.getId())))
            .collect(Collectors.toList());
    }
}
