/**
 * 商品模块 API
 * 对接后端 /product 接口
 */
import request from '@/utils/request'

/**
 * 获取商品列表（分页）
 * @param {Object} params - 查询参数
 * {
 *   name: string,        // 商品名称（模糊搜索）
 *   categoryId: number,  // 分类ID
 *   minPrice: number,    // 最低价格
 *   maxPrice: number,    // 最高价格
 *   status: number,      // 状态
 *   sortField: string,   // 排序字段
 *   sortOrder: string,   // 排序方式 asc/desc
 *   pageNum: number,     // 页码
 *   pageSize: number     // 每页大小
 * }
 */
export function getProductList(params) {
  return request.get('/product/list', params)
}

/**
 * 获取商品详情
 * @param {number} productId - 商品ID
 */
export function getProductDetail(productId) {
  return request.get(`/product/${productId}`)
}

/**
 * 根据分类获取商品
 * @param {number} categoryId - 分类ID
 */
export function getProductsByCategory(categoryId) {
  return request.get(`/product/category/${categoryId}`)
}

/**
 * 搜索商品
 * @param {string} keyword - 搜索关键词
 */
export function searchProducts(keyword) {
  return request.get('/product/search', { keyword })
}

/**
 * 获取热门商品
 * @param {number} limit - 数量限制
 */
export function getHotProducts(limit = 10) {
  return request.get('/product/hot', { limit })
}

/**
 * 获取新品推荐
 * @param {number} limit - 数量限制
 */
export function getNewProducts(limit = 10) {
  return request.get('/product/new', { limit })
}

/**
 * 批量查询商品
 * @param {Array} productIds - 商品ID列表
 */
export function batchGetProducts(productIds) {
  return request.post('/product/batch', productIds)
}
