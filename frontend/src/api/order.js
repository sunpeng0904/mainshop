/**
 * 订单模块 API
 * 对接后端 /order 接口
 */
import request from '@/utils/request'

/**
 * 获取订单列表
 * @param {Object} params - 查询参数 { status, pageNum, pageSize }
 */
export function getOrderList(params = {}) {
  return request.get('/order/list', params)
}

/**
 * 获取订单详情
 * @param {number} orderId - 订单ID
 */
export function getOrderDetail(orderId) {
  return request.get(`/order/${orderId}`)
}

/**
 * 根据订单编号获取订单详情
 * @param {string} orderNo - 订单编号
 */
export function getOrderByOrderNo(orderNo) {
  return request.get(`/order/no/${orderNo}`)
}

/**
 * 创建订单
 * @param {Object} data - 订单数据
 * {
 *   cartIds: [],          // 购物车商品ID列表
 *   addressId: number,    // 收货地址ID
 *   remark: string,       // 订单备注
 *   payType: number,      // 支付方式 1-支付宝 2-微信
 *   couponId: number,     // 优惠券ID（可选）
 *   receiverName: string, // 收货人姓名
 *   receiverPhone: string,// 收货人电话
 *   receiverAddress: string // 收货地址
 * }
 */
export function createOrder(data) {
  return request.post('/order/create', data)
}

/**
 * 取消订单
 * @param {number} orderId - 订单ID
 * @param {string} reason - 取消原因（可选）
 */
export function cancelOrder(orderId, reason) {
  return request.put(`/order/cancel/${orderId}`, null, {
    params: { reason }
  })
}

/**
 * 确认收货
 * @param {number} orderId - 订单ID
 */
export function confirmOrder(orderId) {
  return request.put(`/order/confirm/${orderId}`)
}

/**
 * 删除订单
 * @param {number} orderId - 订单ID
 */
export function deleteOrder(orderId) {
  return request.delete(`/order/${orderId}`)
}

/**
 * 获取订单数量统计
 */
export function getOrderStatistics() {
  return request.get('/order/statistics')
}

// 订单状态映射
export const orderStatusMap = {
  0: { text: '待付款', type: 'warning' },
  1: { text: '待发货', type: 'primary' },
  2: { text: '已发货', type: 'info' },
  3: { text: '已完成', type: 'success' },
  4: { text: '已取消', type: 'danger' }
}

/**
 * 获取订单状态映射
 */
export function getOrderStatusMap() {
  return orderStatusMap
}
