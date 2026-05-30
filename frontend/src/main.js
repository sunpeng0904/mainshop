import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import '@/styles/index.scss'
import i18n from './locales'

// 图片懒加载指令
const lazyLoad = {
  mounted(el, binding) {
    const observer = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          const img = el
          img.src = binding.value
          img.classList.add('loaded')
          observer.unobserve(el)
        }
      })
    }, { threshold: 0.1 })
    observer.observe(el)
  }
}

// 抑制 ResizeObserver 循环错误（Element Plus 常见问题，无害）
const debounce = (fn, delay) => {
  let timer = null
  return function (...args) {
    if (timer) clearTimeout(timer)
    timer = setTimeout(() => fn.apply(this, args), delay)
  }
}

const _ResizeObserver = window.ResizeObserver
window.ResizeObserver = class ResizeObserver extends _ResizeObserver {
  constructor(callback) {
    callback = debounce(callback, 16)
    super(callback)
  }
}

// 创建Vue实例
const app = createApp(App)

// 注册懒加载指令
app.directive('lazy', lazyLoad)

// 注册Element Plus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 使用插件
app.use(ElementPlus)
app.use(store)
app.use(router)
app.use(i18n)

// 全局配置
app.config.globalProperties.$ELEMENT = {
  size: 'default',
  zIndex: 3000
}

// 挂载应用
app.mount('#app')

// 开发环境日志
if (process.env.NODE_ENV === 'development') {
  console.log('Online Mall Frontend started')
  console.log('Environment:', process.env.NODE_ENV)
  console.log('API Base URL:', process.env.VUE_APP_API_BASE_URL)
}
