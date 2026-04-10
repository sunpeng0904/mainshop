/**
 * 分类模块 API
 * 对接后端 /category 接口
 */
import request from '@/utils/request'

/**
 * 获取分类树（带子分类）
 */
export function getCategoryTree() {
  return request.get('/category/tree')
}

/**
 * 获取一级分类列表
 */
export function getTopCategories() {
  return request.get('/category/top')
}

/**
 * 获取子分类列表
 * @param {number} parentId - 父分类ID
 */
export function getChildrenCategories(parentId) {
  return request.get(`/category/children/${parentId}`)
}

/**
 * 获取分类详情
 * @param {number} categoryId - 分类ID
 */
export function getCategoryDetail(categoryId) {
  return request.get(`/category/${categoryId}`)
}
