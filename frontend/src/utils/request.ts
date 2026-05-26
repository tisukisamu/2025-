import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse, InternalAxiosRequestConfig } from 'axios'
import { Result } from '@/types/response'

const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'

const instance: AxiosInstance = axios.create({
  baseURL: BASE_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

const normalizePageData = (data: any) => {
  if (!data || typeof data !== 'object') {
    return data
  }

  const hasSpringPageShape = Array.isArray(data.content) && data.totalElements !== undefined
  if (!hasSpringPageShape) {
    return data
  }

  return {
    ...data,
    list: Array.isArray(data.list) ? data.list : data.content,
    total: typeof data.total === 'number' ? data.total : data.totalElements,
    page: typeof data.page === 'number' ? data.page : data.number,
    size: typeof data.size === 'number' ? data.size : data.content.length
  }
}

instance.interceptors.request.use(
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

instance.interceptors.response.use(
  (response: AxiosResponse<Result>) => {
    const res = response.data
    if (res.code === 200) {
      res.data = normalizePageData(res.data)
      return res as any
    }
    if (res.code === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
      return Promise.reject(new Error(res.message || '未授权'))
    }
    return Promise.reject(new Error(res.message || '请求失败'))
  },
  (error) => {
    const message = error.response?.data?.message || error.message || '网络错误'
    return Promise.reject(new Error(message))
  }
)

export const request = {
  get<T = any>(url: string, config?: AxiosRequestConfig): Promise<Result<T>> {
    return instance.get(url, config)
  },

  post<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<Result<T>> {
    return instance.post(url, data, config)
  },

  put<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<Result<T>> {
    return instance.put(url, data, config)
  },

  delete<T = any>(url: string, config?: AxiosRequestConfig): Promise<Result<T>> {
    return instance.delete(url, config)
  },

  upload<T = any>(url: string, file: File, type: string = 'voucher'): Promise<Result<T>> {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('type', type)
    return instance.post(url, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  }
}

export default instance
