/**
 * 用户模块 API
 */
import request from '@/utils/request'

/**
 * 用户登录
 * @param {Object} data - { username, password, rememberMe }
 */
export function login(data) {
  return request.post('/user/login', data)
}

/**
 * 用户注册
 * @param {Object} data - { username, password, confirmPassword, email, phone, nickname, gender }
 */
export function register(data) {
  return request.post('/user/register', data)
}

/**
 * 用户登出
 */
export function logout() {
  return request.post('/user/logout')
}

/**
 * 获取用户信息
 */
export function getUserInfo() {
  return request.get('/user/info')
}

/**
 * 更新用户信息
 * @param {Object} data - 用户信息对象
 */
export function updateUserInfo(data) {
  return request.put('/user/info', data)
}

/**
 * 修改密码
 * @param {string} oldPassword - 旧密码
 * @param {string} newPassword - 新密码
 */
export function changePassword(oldPassword, newPassword) {
  return request.put('/user/password', { oldPassword, newPassword })
}

/**
 * 重置密码
 * @param {string} email - 邮箱
 * @param {string} newPassword - 新密码
 */
export function resetPassword(email, newPassword) {
  return request.post('/user/password/reset', { email, newPassword })
}

/**
 * 检查用户名是否存在
 * @param {string} username - 用户名
 */
export function checkUsername(username) {
  return request.get('/user/check/username', { username })
}

/**
 * 检查邮箱是否存在
 * @param {string} email - 邮箱
 */
export function checkEmail(email) {
  return request.get('/user/check/email', { email })
}

/**
 * 检查手机号是否存在
 * @param {string} phone - 手机号
 */
export function checkPhone(phone) {
  return request.get('/user/check/phone', { phone })
}
