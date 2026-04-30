/**
 * 徒步路线相关接口
 */
import request from '@/utils/request'

/**
 * 获取推荐路线列表
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.pageSize - 每页数量
 * @param {string} params.difficulty - 难度等级 (easy/medium/hard)
 * @param {string} params.location - 地区
 */
export const getHikingRoutes = (params = {}) => {
  return request.get('/hiking/routes', params)
}

/**
 * 获取路线详情
 * @param {number} id - 路线ID
 */
export const getRouteDetail = (id) => {
  return request.get(`/hiking/routes/${id}`)
}

/**
 * 获取路线轨迹数据
 * @param {number} routeId - 路线ID
 */
export const getRouteTrack = (routeId) => {
  return request.get(`/hiking/routes/${routeId}/track`)
}

/**
 * 收藏路线
 * @param {number} routeId - 路线ID
 */
export const favoriteRoute = (routeId) => {
  return request.post(`/hiking/routes/${routeId}/favorite`)
}

/**
 * 取消收藏路线
 * @param {number} routeId - 路线ID
 */
export const unfavoriteRoute = (routeId) => {
  return request.delete(`/hiking/routes/${routeId}/favorite`)
}

/**
 * 获取用户评价列表
 * @param {number} routeId - 路线ID
 * @param {Object} params - 分页参数
 */
export const getRouteReviews = (routeId, params = {}) => {
  return request.get(`/hiking/routes/${routeId}/reviews`, params)
}

/**
 * 提交评价
 * @param {number} routeId - 路线ID
 * @param {Object} data - 评价数据
 */
export const submitReview = (routeId, data) => {
  return request.post(`/hiking/routes/${routeId}/reviews`, data)
}

/**
 * 获取用户收藏的路线
 */
export const getFavoriteRoutes = () => {
  return request.get('/hiking/favorites')
}

// ==================== 后台管理接口 ====================

/**
 * 获取管理后台路线列表
 * @param {Object} params - 查询参数
 */
export const getAdminHikingRoutes = (params = {}) => {
  return request.get('/admin/hiking/routes', params)
}

/**
 * 创建路线
 * @param {Object} data - 路线数据
 */
export const createHikingRoute = (data) => {
  return request.post('/admin/hiking/routes', data)
}

/**
 * 更新路线
 * @param {number} id - 路线ID
 * @param {Object} data - 路线数据
 */
export const updateHikingRoute = (id, data) => {
  return request.put(`/admin/hiking/routes/${id}`, data)
}

/**
 * 删除路线
 * @param {number} id - 路线ID
 */
export const deleteHikingRoute = (id) => {
  return request.delete(`/admin/hiking/routes/${id}`)
}

/**
 * 切换路线状态
 * @param {number} id - 路线ID
 * @param {string} status - 状态
 */
export const toggleRouteStatus = (id, status) => {
  return request.put(`/admin/hiking/routes/${id}/status`, null, { params: { status } })
}

/**
 * 获取管理后台评价列表
 * @param {Object} params - 查询参数
 */
export const getAdminHikingReviews = (params = {}) => {
  return request.get('/admin/hiking/reviews', params)
}

/**
 * 审核通过评价
 * @param {number} id - 评价ID
 */
export const approveHikingReview = (id) => {
  return request.put(`/admin/hiking/reviews/${id}/approve`)
}

/**
 * 拒绝评价
 * @param {number} id - 评价ID
 */
export const rejectHikingReview = (id) => {
  return request.put(`/admin/hiking/reviews/${id}/reject`)
}

/**
 * 回复评价
 * @param {number} id - 评价ID
 * @param {string} reply - 回复内容
 */
export const replyHikingReview = (id, reply) => {
  return request.put(`/admin/hiking/reviews/${id}/reply`, { reply })
}

/**
 * 删除评价
 * @param {number} id - 评价ID
 */
export const deleteHikingReview = (id) => {
  return request.delete(`/admin/hiking/reviews/${id}`)
}

/**
 * 获取评分统计
 * @param {number} routeId - 路线ID
 */
export const getHikingRatingStats = (routeId) => {
  return request.get(`/admin/hiking/routes/${routeId}/rating-stats`)
}
