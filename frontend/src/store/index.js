import { createStore } from 'vuex'
import createPersistedState from 'vuex-persistedstate'

// 模块
import user from './modules/user'
import cart from './modules/cart'
import product from './modules/product'

// 创建Store实例
const store = createStore({
  modules: {
    user,
    cart,
    product
  },
  
  // 状态持久化
  plugins: [
    createPersistedState({
      key: 'online-mall',
      paths: ['user', 'cart']
    })
  ],
  
  // 严格模式（仅开发环境）
  strict: process.env.NODE_ENV !== 'production'
})

export default store