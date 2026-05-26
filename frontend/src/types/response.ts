export interface Result<T = any> {
  code: number
  message: string
  data: T
  timestamp: number
}

export interface PageResult<T = any> {
  total: number
  page: number
  size: number
  list: T[]
  content?: T[]
  totalElements?: number
  number?: number
}

export interface LoginResponse {
  token: string
  user: import('./index').User
}

export interface UploadResponse {
  url: string
  fileName: string
  fileSize: number
}
