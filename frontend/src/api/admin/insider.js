/**
 * 内幕信息知情人登记 API
 */
import request from '@/utils/request'

/**
 * 获取内幕信息知情人列表
 * @param {Object} params - 查询参数
 */
export function getInsiderList(params) {
  return request.get('/admin/insider/list', params)
}

/**
 * 获取内幕信息知情人详情
 * @param {number} id - 记录ID
 */
export function getInsiderDetail(id) {
  return request.get(`/admin/insider/${id}`)
}

/**
 * 创建内幕信息知情人登记
 * @param {Object} data - 登记数据
 */
export function createInsider(data) {
  return request.post('/admin/insider', data)
}

/**
 * 更新内幕信息知情人登记
 * @param {number} id - 记录ID
 * @param {Object} data - 登记数据
 */
export function updateInsider(id, data) {
  return request.put(`/admin/insider/${id}`, data)
}

/**
 * 删除内幕信息知情人登记
 * @param {number} id - 记录ID
 */
export function deleteInsider(id) {
  return request.delete(`/admin/insider/${id}`)
}

/**
 * 更新状态
 * @param {number} id - 记录ID
 * @param {string} status - 状态值
 */
export function updateInsiderStatus(id, status) {
  return request.put(`/admin/insider/${id}/status`, null, { params: { status } })
}
