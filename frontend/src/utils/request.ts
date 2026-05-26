import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse, InternalAxiosRequestConfig } from 'axios'
import { message } from 'ant-design-vue'
import type { ApiResponse } from '../types'

interface RequestConfig extends AxiosRequestConfig {
  showErrorMessage?: boolean
}

const request: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

let errorMessageShown = false
const showError = (msg: string, duration: number = 3): void => {
  if (!errorMessageShown) {
    errorMessageShown = true
    message.error(msg, duration)
    setTimeout(() => {
      errorMessageShown = false
    }, duration * 1000)
  }
}

request.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem('token')
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>) => {
    const data = response.data
    if (data.code && data.code !== 200) {
      showError(data.message || '操作失败')
      return Promise.reject(new Error(data.message))
    }
    return data as any
  },
  (error) => {
    const { response, request } = error
    
    if (response) {
      const data = response.data || {}
      const errorMsg = data.message || '请求失败'
      
      switch (response.status) {
        case 400:
          showError(errorMsg || '请求参数错误')
          break
        case 401:
          showError(errorMsg || '登录已过期，请重新登录')
          localStorage.removeItem('token')
          setTimeout(() => {
            window.location.href = '/login'
          }, 1500)
          break
        case 403:
          showError(errorMsg || '没有权限执行此操作')
          break
        case 404:
          showError(errorMsg || '请求的资源不存在')
          break
        case 405:
          showError(errorMsg || '请求方法不允许')
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
          showError(errorMsg || '服务器内部错误，请稍后重试')
          break
        case 502:
          showError(errorMsg || '网关错误，请稍后重试')
          break
        case 503:
          showError(errorMsg || '服务暂时不可用，请稍后重试')
          break
        case 504:
          showError(errorMsg || '网关超时，请稍后重试')
          break
        default:
          showError(errorMsg)
      }
      
      console.error(`[HTTP ${response.status}]`, error.config?.url, data)
      
    } else if (request) {
      if (error.code === 'ECONNABORTED') {
        showError('请求超时，请检查网络连接')
      } else if (error.code === 'Network Error') {
        showError('网络连接失败，请检查网络')
      } else {
        showError('服务器无响应')
      }
      console.error('[Network Error]', error.config?.url, error.message)
      
    } else {
      showError('请求配置错误')
      console.error('[Request Error]', error.message)
    }
    
    return Promise.reject(error)
  }
)

export default request
