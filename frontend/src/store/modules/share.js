import { getUnreadCount } from '@/api/share'

const state = {
  // 未读通知数量
  unreadCount: {
    total: 0,
    LIKE: 0,
    COMMENT: 0,
    FORWARD: 0,
    FOLLOW: 0,
    MENTION: 0
  },
  // 当前发布的分享草稿
  draft: null
}

const getters = {
  totalUnreadCount: (state) => state.unreadCount.total,
  unreadCountByType: (state) => (type) => state.unreadCount[type] || 0,
  hasDraft: (state) => state.draft !== null
}

const mutations = {
  SET_UNREAD_COUNT(state, data) {
    state.unreadCount = { ...state.unreadCount, ...data }
  },
  SET_DRAFT(state, draft) {
    state.draft = draft
  },
  CLEAR_DRAFT(state) {
    state.draft = null
  }
}

const actions = {
  async fetchUnreadCount({ commit }) {
    try {
      const res = await getUnreadCount()
      commit('SET_UNREAD_COUNT', res.data)
      return res.data
    } catch (error) {
      console.error('获取未读通知数量失败:', error)
      return null
    }
  },
  saveDraft({ commit }, draft) {
    commit('SET_DRAFT', draft)
  },
  clearDraft({ commit }) {
    commit('CLEAR_DRAFT')
  }
}

export default {
  namespaced: true,
  state,
  getters,
  mutations,
  actions
}
