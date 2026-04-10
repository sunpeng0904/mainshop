/**
 * 购物车状态管理
 */
import {
  getCartList,
  addToCart,
  updateCartQuantity,
  removeCartItem,
  batchRemoveCartItems,
  selectCartItem,
  selectAllCart,
  clearCart,
  getCartCount
} from '@/api/cart'

const state = {
  cartList: [],
  cartCount: 0
}

const getters = {
  // 选中的商品
  selectedItems: state => {
    return state.cartList.filter(item => item.selected === 1)
  },
  // 选中商品总数
  selectedCount: state => {
    return state.cartList
      .filter(item => item.selected === 1)
      .reduce((sum, item) => sum + item.quantity, 0)
  },
  // 选中商品总价
  selectedTotalPrice: state => {
    return state.cartList
      .filter(item => item.selected === 1)
      .reduce((sum, item) => sum + Number(item.productPrice) * item.quantity, 0)
      .toFixed(2)
  },
  // 是否全选
  isAllSelected: state => {
    return state.cartList.length > 0 &&
      state.cartList.every(item => item.selected === 1)
  }
}

const mutations = {
  SET_CART_LIST: (state, list) => {
    state.cartList = list || []
    state.cartCount = state.cartList.reduce((sum, item) => sum + item.quantity, 0)
  },
  SET_CART_COUNT: (state, count) => {
    state.cartCount = count || 0
  },
  CLEAR_CART: (state) => {
    state.cartList = []
    state.cartCount = 0
  }
}

const actions = {
  // 获取购物车列表
  async getCartList({ commit }) {
    try {
      const response = await getCartList()
      commit('SET_CART_LIST', response.data)
      return response
    } catch (error) {
      return Promise.reject(error)
    }
  },

  // 获取购物车数量
  async getCartCount({ commit }) {
    try {
      const response = await getCartCount()
      commit('SET_CART_COUNT', response.data)
      return response
    } catch (error) {
      return Promise.reject(error)
    }
  },

  // 添加商品到购物车
  async addToCart({ dispatch }, data) {
    try {
      const response = await addToCart(data)
      await dispatch('getCartList')
      return response
    } catch (error) {
      return Promise.reject(error)
    }
  },

  // 更新商品数量
  async updateQuantity({ dispatch }, data) {
    try {
      const response = await updateCartQuantity(data)
      await dispatch('getCartList')
      return response
    } catch (error) {
      return Promise.reject(error)
    }
  },

  // 删除商品
  async removeItem({ dispatch }, cartId) {
    try {
      const response = await removeCartItem(cartId)
      await dispatch('getCartList')
      return response
    } catch (error) {
      return Promise.reject(error)
    }
  },

  // 批量删除
  async batchRemove({ dispatch }, cartIds) {
    try {
      const response = await batchRemoveCartItems(cartIds)
      await dispatch('getCartList')
      return response
    } catch (error) {
      return Promise.reject(error)
    }
  },

  // 选中/取消选中
  async selectItem({ dispatch }, data) {
    try {
      const response = await selectCartItem(data)
      await dispatch('getCartList')
      return response
    } catch (error) {
      return Promise.reject(error)
    }
  },

  // 全选/取消全选
  async selectAll({ dispatch }, selected) {
    try {
      const response = await selectAllCart(selected)
      await dispatch('getCartList')
      return response
    } catch (error) {
      return Promise.reject(error)
    }
  },

  // 清空购物车
  async clearCart({ commit }) {
    try {
      await clearCart()
      commit('CLEAR_CART')
    } catch (error) {
      return Promise.reject(error)
    }
  }
}

export default {
  namespaced: true,
  state,
  getters,
  mutations,
  actions
}
