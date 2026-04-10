import axios from 'axios'
import { ElMessage } from 'element-plus'
import store from '@/store'
import { getToken } from '@/utils/auth'

// 创建axios实例
const service = axios.create({
  baseURL: process.env.VUE_APP_API_BASE_URL || '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json;charset=utf-8'
  }
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 添加token
    const token = getToken()
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    
    // 添加时间戳防止缓存
    if (config.method === 'get') {
      config.params = {
        ...config.params,
        _t: Date.now()
      }
    }
    
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    
    // 业务成功
    if (res.code === 200) {
      return res
    }
    
    // 业务错误
    if (res.code === 401) {
      // token过期
      ElMessage.error('登录已过期，请重新登录')
      store.dispatch('user/logout').then(() => {
        window.location.href = '/login'
      })
      return Promise.reject(new Error(res.message || 'Error'))
    }
    
    if (res.code === 403) {
      ElMessage.error('权限不足')
      return Promise.reject(new Error(res.message || 'Error'))
    }
    
    ElMessage.error(res.message || '请求失败')
    return Promise.reject(new Error(res.message || 'Error'))
  },
  error => {
    console.error('响应错误:', error)
    
    if (error.response) {
      // 服务器返回错误
      switch (error.response.status) {
        case 400:
          ElMessage.error('请求参数错误')
          break
        case 401:
          ElMessage.error('未授权，请重新登录')
          store.dispatch('user/logout').then(() => {
            window.location.href = '/login'
          })
          break
        case 403:
          ElMessage.error('拒绝访问')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器内部错误')
          break
        case 502:
          ElMessage.error('网关错误')
          break
        case 503:
          ElMessage.error('服务不可用')
          break
        case 504:
          ElMessage.error('网关超时')
          break
        default:
          ElMessage.error(`请求失败: ${error.response.status}`)
      }
    } else if (error.request) {
      // 请求发送但无响应
      if (error.code === 'ECONNABORTED') {
        ElMessage.error('请求超时，请检查网络连接')
      } else {
        ElMessage.error('网络错误，请检查网络连接')
      }
    } else {
      // 请求配置错误
      ElMessage.error('请求配置错误')
    }
    
    return Promise.reject(error)
  }
)

// 封装请求方法
const request = {
  get(url, params = {}, config = {}) {
    return service.get(url, { params, ...config })
  },
  
  post(url, data = {}, config = {}) {
    return service.post(url, data, config)
  },
  
  put(url, data = {}, config = {}) {
    return service.put(url, data, config)
  },
  
  delete(url, params = {}, config = {}) {
    return service.delete(url, { params, ...config })
  },
  
  patch(url, data = {}, config = {}) {
    return service.patch(url, data, config)
  },
  
  // 文件上传
  upload(url, file, config = {}) {
    const formData = new FormData()
    formData.append('file', file)
    
    return service.post(url, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      ...config
    })
  },
  
  // 下载文件
  download(url, params = {}, filename = 'download') {
    return service.get(url, {
      params,
      responseType: 'blob'
    }).then(response => {
      const blob = new Blob([response.data])
      const link = document.createElement('a')
      link.href = window.URL.createObjectURL(blob)
      link.download = filename
      link.click()
      window.URL.revokeObjectURL(link.href)
    })
  }
}

export default request