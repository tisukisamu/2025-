import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios'
import { message } from 'ant-design-vue'
import type { ApiResponse } from '@/types/common'

const request: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

request.interceptors.request.use(
  (config) => {
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

request.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>) => {
    return response.data
  },
  (error) => {
    if (error.response) {
      const serverMessage = error.response.data?.message
      switch (error.response.status) {
        case 401:
          message.error(serverMessage || '登录已过期，请重新登录')
          localStorage.removeItem('token')
          window.location.href = '/login'
          break
        case 403:
          message.error(serverMessage || '没有权限访问')
          break
        case 404:
          message.error(serverMessage || '请求的资源不存在')
          break
        case 500:
          message.error(serverMessage || '服务器错误')
          break
        default:
          message.error(serverMessage || '请求失败')
          break
      }
    } else {
      message.error('网络错误，请检查网络连接')
    }
    return Promise.reject(error)
  }
)

export default request

export const get = <T = any>(url: string, config?: AxiosRequestConfig): Promise<ApiResponse<T>> => {
  return request.get(url, config)
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
