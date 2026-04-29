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
  return request({
    url: '/hiking/routes',
    method: 'get',
    params
  })
}

/**
 * 获取路线详情
 * @param {number} id - 路线ID
 */
export const getRouteDetail = (id) => {
  return request({
    url: `/hiking/routes/${id}`,
    method: 'get'
  })
}

/**
 * 获取路线轨迹数据
 * @param {number} routeId - 路线ID
 */
export const getRouteTrack = (routeId) => {
  return request({
    url: `/hiking/routes/${routeId}/track`,
    method: 'get'
  })
}

/**
 * 收藏路线
 * @param {number} routeId - 路线ID
 */
export const favoriteRoute = (routeId) => {
  return request({
    url: `/hiking/routes/${routeId}/favorite`,
    method: 'post'
  })
}

/**
 * 取消收藏路线
 * @param {number} routeId - 路线ID
 */
export const unfavoriteRoute = (routeId) => {
  return request({
    url: `/hiking/routes/${routeId}/favorite`,
    method: 'delete'
  })
}

/**
 * 获取用户评价列表
 * @param {number} routeId - 路线ID
 * @param {Object} params - 分页参数
 */
export const getRouteReviews = (routeId, params = {}) => {
  return request({
    url: `/hiking/routes/${routeId}/reviews`,
    method: 'get',
    params
  })
}

/**
 * 提交评价
 * @param {number} routeId - 路线ID
 * @param {Object} data - 评价数据
 */
export const submitReview = (routeId, data) => {
  return request({
    url: `/hiking/routes/${routeId}/reviews`,
    method: 'post',
    data
  })
}

/**
 * 获取用户收藏的路线
 */
export const getFavoriteRoutes = () => {
  return request({
    url: '/hiking/favorites',
    method: 'get'
  })
}

// ==================== 后台管理接口 ====================

/**
 * 获取管理后台路线列表
 * @param {Object} params - 查询参数
 */
export const getAdminHikingRoutes = (params = {}) => {
  return request({
    url: '/admin/hiking/routes',
    method: 'get',
    params
  })
}

/**
 * 创建路线
 * @param {Object} data - 路线数据
 */
export const createHikingRoute = (data) => {
  return request({
    url: '/admin/hiking/routes',
    method: 'post',
    data
  })
}

/**
 * 更新路线
 * @param {number} id - 路线ID
 * @param {Object} data - 路线数据
 */
export const updateHikingRoute = (id, data) => {
  return request({
    url: `/admin/hiking/routes/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除路线
 * @param {number} id - 路线ID
 */
export const deleteHikingRoute = (id) => {
  return request({
    url: `/admin/hiking/routes/${id}`,
    method: 'delete'
  })
}

/**
 * 切换路线状态
 * @param {number} id - 路线ID
 * @param {string} status - 状态
 */
export const toggleRouteStatus = (id, status) => {
  return request({
    url: `/admin/hiking/routes/${id}/status`,
    method: 'put',
    params: { status }
  })
}

/**
 * 获取管理后台评价列表
 * @param {Object} params - 查询参数
 */
export const getAdminHikingReviews = (params = {}) => {
  return request({
    url: '/admin/hiking/reviews',
    method: 'get',
    params
  })
}

/**
 * 审核通过评价
 * @param {number} id - 评价ID
 */
export const approveHikingReview = (id) => {
  return request({
    url: `/admin/hiking/reviews/${id}/approve`,
    method: 'put'
  })
}

/**
 * 拒绝评价
 * @param {number} id - 评价ID
 */
export const rejectHikingReview = (id) => {
  return request({
    url: `/admin/hiking/reviews/${id}/reject`,
    method: 'put'
  })
}

/**
 * 回复评价
 * @param {number} id - 评价ID
 * @param {string} reply - 回复内容
 */
export const replyHikingReview = (id, reply) => {
  return request({
    url: `/admin/hiking/reviews/${id}/reply`,
    method: 'put',
    data: { reply }
  })
}

/**
 * 删除评价
 * @param {number} id - 评价ID
 */
export const deleteHikingReview = (id) => {
  return request({
    url: `/admin/hiking/reviews/${id}`,
    method: 'delete'
  })
}

/**
 * 获取评分统计
 * @param {number} routeId - 路线ID
 */
export const getHikingRatingStats = (routeId) => {
  return request({
    url: `/admin/hiking/routes/${routeId}/rating-stats`,
    method: 'get'
  })
}
