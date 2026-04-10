import { login, logout, getUserInfo, register } from '@/api/user'
import { getToken, setToken, removeToken } from '@/utils/auth'

const state = {
  token: getToken(),
  userInfo: null,
  permissions: [],
  roles: []
}

const mutations = {
  SET_TOKEN: (state, token) => {
    state.token = token
  },
  SET_USER_INFO: (state, userInfo) => {
    state.userInfo = userInfo
  },
  SET_PERMISSIONS: (state, permissions) => {
    state.permissions = permissions
  },
  SET_ROLES: (state, roles) => {
    state.roles = roles
  },
  CLEAR_USER: (state) => {
    state.token = ''
    state.userInfo = null
    state.permissions = []
    state.roles = []
  }
}

const actions = {
  // 用户登录
  async login({ commit }, userInfo) {
    try {
      const { username, password } = userInfo
      const response = await login({ username, password })
      const token = response.data  // data直接就是token字符串
      commit('SET_TOKEN', token)
      setToken(token)
      return response
    } catch (error) {
      return Promise.reject(error)
    }
  },

  // 用户注册
  async register({ commit }, userInfo) {
    try {
      const response = await register(userInfo)
      return response
    } catch (error) {
      return Promise.reject(error)
    }
  },

  // 获取用户信息
  async getUserInfo({ commit, state }) {
    try {
      const response = await getUserInfo()
      const { data } = response
      commit('SET_USER_INFO', data)
      // 根据用户角色设置权限
      const roles = data.roles || ['user']
      const permissions = data.permissions || []
      commit('SET_PERMISSIONS', permissions)
      commit('SET_ROLES', roles)
      return response
    } catch (error) {
      return Promise.reject(error)
    }
  },

  // 用户退出
  async logout({ commit, state }) {
    try {
      await logout(state.token)
      commit('CLEAR_USER')
      removeToken()
      return Promise.resolve()
    } catch (error) {
      return Promise.reject(error)
    }
  },

  // 清除token
  resetToken({ commit }) {
    return new Promise(resolve => {
      commit('CLEAR_USER')
      removeToken()
      resolve()
    })
  }
}

const getters = {
  token: state => state.token,
  userInfo: state => state.userInfo,
  permissions: state => state.permissions,
  roles: state => state.roles,
  isLogin: state => !!state.token,
  isAdmin: state => {
    return state.roles && state.roles.includes('admin')
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}