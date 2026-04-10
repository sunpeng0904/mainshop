/**
 * 管理员订单模块 API
 */
import request from '@/utils/request'

/**
 * 获取订单列表
 * @param {Object} params - 查询参数 { status, orderNo, pageNum, pageSize }
 */
export function getAdminOrderList(params = {}) {
  return request.get('/admin/order/list', params)
}

/**
 * 获取订单详情
 * @param {number} orderId - 订单ID
 */
export function getAdminOrderDetail(orderId) {
  return request.get(`/admin/order/${orderId}`)
}

/**
 * 发货
 * @param {number} orderId - 订单ID
 * @param {Object} data - { logisticsCompany, logisticsNo }
 */
export function shipOrder(orderId, data = {}) {
  return request.put(`/admin/order/ship/${orderId}`, null, {
    params: data
  })
}

/**
 * 取消订单
 * @param {number} orderId - 订单ID
 * @param {string} reason - 取消原因
 */
export function cancelOrder(orderId, reason) {
  return request.put(`/admin/order/cancel/${orderId}`, null, {
    params: { reason }
  })
}

/**
 * 删除订单
 * @param {number} orderId - 订单ID
 */
export function deleteOrder(orderId) {
  return request.delete(`/admin/order/${orderId}`)
}

/**
 * 获取订单统计
 */
export function getOrderStatistics() {
  return request.get('/admin/order/statistics')
}
