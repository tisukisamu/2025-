import axios from 'axios'
import { message } from 'ant-design-vue'

// 创建 axios 实例
const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 可以在这里添加token等认证信息
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 统一的错误消息提示（防止重复提示）
let errorMessageShown = false
const showError = (msg, duration = 3) => {
  if (!errorMessageShown) {
    errorMessageShown = true
    message.error(msg, duration)
    setTimeout(() => {
      errorMessageShown = false
    }, duration * 1000)
  }
}

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    // 统一处理后端返回的错误码
    const data = response.data
    if (data.code && data.code !== 200) {
      showError(data.message || '操作失败')
      return Promise.reject(new Error(data.message))
    }
    return data
  },
  (error) => {
    const { response, request } = error
    
    if (response) {
      // 服务器返回了错误状态码
      const data = response.data || {}
      const errorMsg = data.message || '请求失败'
      
      switch (response.status) {
        case 400:
          showError(errorMsg || '请求参数错误')
          break
        case 401:
          // 优先显示后端返回的具体错误（如"用户名或密码错误"）
          showError(errorMsg || '登录已过期，请重新登录')
          // 只有登录过期才清除token并跳转
          if (errorMsg && errorMsg.includes('过期')) {
            localStorage.removeItem('token')
            setTimeout(() => {
              window.location.href = '/login'
            }, 1500)
          }
          break
        case 403:
          showError(errorMsg || '没有权限执行此操作')
          break
        case 404:
          showError('请求的资源不存在')
          break
        case 405:
          showError('请求方法不允许')
          break
        case 408:
          showError('请求超时，请稍后重试')
          break
        case 422:
          showError(`数据验证失败: ${errorMsg}`)
          break
        case 429:
          showError('请求过于频繁，请稍后再试')
          break
        case 500:
          showError('服务器内部错误，请稍后重试')
          break
        case 502:
          showError('网关错误，请稍后重试')
          break
        case 503:
          showError('服务暂时不可用，请稍后重试')
          break
        case 504:
          showError('网关超时，请稍后重试')
          break
        default:
          showError(errorMsg)
      }
      
      // 记录错误日志
      console.error(`[HTTP ${response.status}]`, error.config?.url, data)
      
    } else if (request) {
      // 请求已发送但没有收到响应
      if (error.code === 'ECONNABORTED') {
        showError('请求超时，请检查网络连接')
      } else if (error.code === 'Network Error') {
        showError('网络连接失败，请检查网络')
      } else {
        showError('服务器无响应')
      }
      console.error('[Network Error]', error.config?.url, error.message)
      
    } else {
      // 请求配置出错
      showError('请求配置错误')
      console.error('[Request Error]', error.message)
    }
    
    return Promise.reject(error)
  }
)

export default request
