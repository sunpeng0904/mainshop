/**
 * 支付模块 API
 * 对接后端 /payment 接口
 */
import request from '@/utils/request'

/**
 * 创建支付
 * @param {Object} data - { orderId, payType }
 * payType: 1-支付宝 2-微信
 */
export function createPayment(data) {
  return request.post('/payment/create', data)
}

/**
 * 模拟支付成功（用于测试）
 * @param {string} paymentNo - 支付流水号
 */
export function mockPaymentSuccess(paymentNo) {
  return request.post(`/payment/mock-success/${paymentNo}`)
}

/**
 * 申请退款
 * @param {number} orderId - 订单ID
 * @param {string} reason - 退款原因（可选）
 */
export function refund(orderId, reason) {
  return request.post(`/payment/refund/${orderId}`, null, {
    params: { reason }
  })
}

/**
 * 根据订单ID获取支付记录
 * @param {number} orderId - 订单ID
 */
export function getPaymentByOrderId(orderId) {
  return request.get(`/payment/order/${orderId}`)
}

/**
 * 根据支付流水号获取支付记录
 * @param {string} paymentNo - 支付流水号
 */
export function getPaymentByPaymentNo(paymentNo) {
  return request.get(`/payment/${paymentNo}`)
}

// 支付状态映射
export const paymentStatusMap = {
  0: { text: '待支付', type: 'warning' },
  1: { text: '支付成功', type: 'success' },
  2: { text: '支付失败', type: 'danger' }
}

// 支付方式映射
export const payTypeMap = {
  1: '支付宝',
  2: '微信'
}
