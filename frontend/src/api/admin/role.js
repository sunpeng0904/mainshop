/**
 * 管理员角色模块 API
 */
import request from '@/utils/request'

/**
 * 获取所有角色
 */
export function getRoleList() {
  return request.get('/admin/role/list')
}

/**
 * 创建角色
 * @param {Object} data - { roleCode, roleName, description, sort }
 */
export function createRole(data) {
  return request.post('/admin/role/create', data)
}

/**
 * 更新角色
 * @param {number} roleId - 角色ID
 * @param {Object} data - { roleName, description, sort, status }
 */
export function updateRole(roleId, data) {
  return request.put(`/admin/role/${roleId}`, data)
}

/**
 * 删除角色
 * @param {number} roleId - 角色ID
 */
export function deleteRole(roleId) {
  return request.delete(`/admin/role/${roleId}`)
}

/**
 * 给用户分配角色
 * @param {number} userId - 用户ID
 * @param {number} roleId - 角色ID
 */
export function assignRoleToUser(userId, roleId) {
  return request.post(`/admin/role/assign/${userId}/${roleId}`)
}

/**
 * 移除用户角色
 * @param {number} userId - 用户ID
 * @param {number} roleId - 角色ID
 */
export function removeRoleFromUser(userId, roleId) {
  return request.delete(`/admin/role/remove/${userId}/${roleId}`)
}

/**
 * 设置用户角色
 * @param {number} userId - 用户ID
 * @param {Array} roleIds - 角色ID列表
 */
export function setUserRoles(userId, roleIds) {
  return request.put(`/admin/role/user/${userId}`, { roleIds })
}
