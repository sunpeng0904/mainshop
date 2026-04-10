/**
 * 商品状态管理
 */
import { getCategoryTree, getHotProducts, getNewProducts } from '@/api/product'
import { getCategoryTree as fetchCategoryTree } from '@/api/category'

const state = {
  categories: [],
  hotProducts: [],
  newProducts: []
}

const mutations = {
  SET_CATEGORIES: (state, categories) => {
    state.categories = categories
  },
  SET_HOT_PRODUCTS: (state, products) => {
    state.hotProducts = products
  },
  SET_NEW_PRODUCTS: (state, products) => {
    state.newProducts = products
  }
}

const actions = {
  // 获取分类树
  async getCategories({ commit, state }) {
    if (state.categories.length > 0) {
      return state.categories
    }

    try {
      const response = await fetchCategoryTree()
      commit('SET_CATEGORIES', response.data || [])
      return response.data
    } catch (error) {
      return Promise.reject(error)
    }
  },

  // 获取热门商品
  async getHotProducts({ commit }, limit = 10) {
    try {
      const response = await getHotProducts(limit)
      commit('SET_HOT_PRODUCTS', response.data || [])
      return response.data
    } catch (error) {
      return Promise.reject(error)
    }
  },

  // 获取新品推荐
  async getNewProducts({ commit }, limit = 10) {
    try {
      const response = await getNewProducts(limit)
      commit('SET_NEW_PRODUCTS', response.data || [])
      return response.data
    } catch (error) {
      return Promise.reject(error)
    }
  }
}

const getters = {
  categories: state => state.categories,
  hotProducts: state => state.hotProducts,
  newProducts: state => state.newProducts
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}
