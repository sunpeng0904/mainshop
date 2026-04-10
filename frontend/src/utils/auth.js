/**
 * Token 存取工具
 */

const TOKEN_KEY = 'mall_token'

export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
}

export function removeToken() {
  localStorage.removeItem(TOKEN_KEY)
}

/**
 * 清除所有登录相关数据
 */
export function clearAuth() {
  removeToken()
  localStorage.removeItem('mall_userInfo')
}
