/**
 * 分类模块 API
 * 对接后端 /category 接口
 */
import request from '@/utils/request'

// ==================== 前台接口 ====================

/**
 * 获取分类树（带子分类，只含启用）
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

// ==================== 管理端接口 ====================

/**
 * 获取分类树（包含禁用）
 */
export function getAdminCategoryTree() {
  return request.get('/admin/category/tree')
}

/**
 * 获取一级分类列表（管理端）
 */
export function getAdminTopCategories() {
  return request.get('/admin/category/top')
}

/**
 * 获取子分类列表（管理端）
 * @param {number} parentId - 父分类ID
 */
export function getAdminChildrenCategories(parentId) {
  return request.get(`/admin/category/children/${parentId}`)
}

/**
 * 获取分类详情（管理端）
 * @param {number} categoryId - 分类ID
 */
export function getAdminCategoryById(categoryId) {
  return request.get(`/admin/category/${categoryId}`)
}

/**
 * 创建分类
 * @param {Object} data - 分类数据
 */
export function createCategory(data) {
  return request.post('/admin/category', data)
}

/**
 * 更新分类
 * @param {number} categoryId - 分类ID
 * @param {Object} data - 分类数据
 */
export function updateCategory(categoryId, data) {
  return request.put(`/admin/category/${categoryId}`, data)
}

/**
 * 删除分类
 * @param {number} categoryId - 分类ID
 */
export function deleteCategory(categoryId) {
  return request.delete(`/admin/category/${categoryId}`)
}

/**
 * 启用分类
 * @param {number} categoryId - 分类ID
 */
export function enableCategory(categoryId) {
  return request.put(`/admin/category/${categoryId}/enable`)
}

/**
 * 禁用分类
 * @param {number} categoryId - 分类ID
 */
export function disableCategory(categoryId) {
  return request.put(`/admin/category/${categoryId}/disable`)
}
