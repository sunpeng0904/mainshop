/**
 * 管理员用户模块 API
 */
import request from '@/utils/request'

/**
 * 获取用户列表
 * @param {Object} params - 查询参数 { username, phone, status, pageNum, pageSize }
 */
export function getAdminUserList(params = {}) {
  return request.get('/admin/user/list', params)
}

/**
 * 获取用户详情
 * @param {number} userId - 用户ID
 */
export function getAdminUserDetail(userId) {
  return request.get(`/admin/user/${userId}`)
}

/**
 * 禁用用户
 * @param {number} userId - 用户ID
 */
export function disableUser(userId) {
  return request.put(`/admin/user/disable/${userId}`)
}

/**
 * 启用用户
 * @param {number} userId - 用户ID
 */
export function enableUser(userId) {
  return request.put(`/admin/user/enable/${userId}`)
}

/**
 * 重置用户密码
 * @param {number} userId - 用户ID
 * @param {string} newPassword - 新密码
 */
export function resetUserPassword(userId, newPassword) {
  return request.put(`/admin/user/reset-password/${userId}`, null, {
    params: { newPassword }
  })
}

/**
 * 获取用户统计
 */
export function getUserStatistics() {
  return request.get('/admin/user/statistics')
}
