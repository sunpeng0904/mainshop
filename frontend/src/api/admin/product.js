/**
 * 管理员商品模块 API
 * 对接后端 /admin/product 接口
 */
import request from '@/utils/request'

/**
 * 获取商品列表(管理端)
 * @param {Object} params - 查询参数
 */
export function getAdminProductList(params) {
  return request.get('/admin/product/list', params)
}

/**
 * 获取商品详情(管理端)
 * @param {number} productId - 商品ID
 */
export function getAdminProductDetail(productId) {
  return request.get(`/admin/product/${productId}`)
}

/**
 * 创建商品
 * @param {Object} data - 商品数据
 */
export function createProduct(data) {
  return request.post('/admin/product', data)
}

/**
 * 更新商品
 * @param {number} productId - 商品ID
 * @param {Object} data - 商品数据
 */
export function updateProduct(productId, data) {
  return request.put(`/admin/product/${productId}`, data)
}

/**
 * 删除商品
 * @param {number} productId - 商品ID
 */
export function deleteProduct(productId) {
  return request.delete(`/admin/product/${productId}`)
}

/**
 * 上架商品
 * @param {number} productId - 商品ID
 */
export function publishProduct(productId) {
  return request.put(`/admin/product/${productId}/publish`)
}

/**
 * 下架商品
 * @param {number} productId - 商品ID
 */
export function unpublishProduct(productId) {
  return request.put(`/admin/product/${productId}/unpublish`)
}

/**
 * 批量上架
 * @param {Array} productIds - 商品ID列表
 */
export function batchPublish(productIds) {
  return request.put('/admin/product/batch/publish', productIds)
}

/**
 * 批量下架
 * @param {Array} productIds - 商品ID列表
 */
export function batchUnpublish(productIds) {
  return request.put('/admin/product/batch/unpublish', productIds)
}

/**
 * 批量删除
 * @param {Array} productIds - 商品ID列表
 */
export function batchDelete(productIds) {
  return request.delete('/admin/product/batch', productIds)
}
