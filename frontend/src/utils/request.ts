import axios from 'axios'
import { message } from 'ant-design-vue'
import type { AxiosInstance, AxiosRequestConfig, AxiosResponse, InternalAxiosRequestConfig } from 'axios'
import type { ApiResponse } from '@/types'

const request: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

let errorMessageShown = false
const showError = (msg: string, duration = 3) => {
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
    console.error('[Request Error]', error)
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>) => {
    const { data } = response
    if (data.code && data.code !== 200) {
      showError(data.message || '操作失败')
      return Promise.reject(new Error(data.message))
    }
    return data
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
          if (errorMsg && (errorMsg.includes('过期') || errorMsg.includes('未登录'))) {
            localStorage.removeItem('token')
            localStorage.removeItem('user')
            setTimeout(() => {
              window.location.href = '/login'
            }, 1500)
          }
          break
        case 403:
          showError(errorMsg || '没有权限执行此操作')
          break
        case 404:
          showError(errorMsg || '请求的资源不存在')
          break
        case 405:
          showError('请求方法不允许')
          break
        case 408:
          showError('请求超时，请稍后重试')
          break
        case 409:
          showError(errorMsg || '资源冲突')
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
      
      console.error(`[HTTP ${response.status}]`, error.config?.url, data)
      
    } else if (request) {
      if (error.code === 'ECONNABORTED') {
        showError('请求超时，请检查网络连接')
      } else if (error.message === 'Network Error') {
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

export const get = <T = any>(url: string, params?: any, config?: AxiosRequestConfig): Promise<ApiResponse<T>> => {
  return request.get(url, { params, ...config })
}

export const post = <T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<ApiResponse<T>> => {
  return request.post(url, data, config)
}

export const put = <T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<ApiResponse<T>> => {
  return request.put(url, data, config)
}

export const del = <T = any>(url: string, config?: AxiosRequestConfig): Promise<ApiResponse<T>> => {
  return request.delete(url, config)
}

export default request
