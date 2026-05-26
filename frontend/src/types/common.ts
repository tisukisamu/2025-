export interface Result<T = any> {
  code: number
  message: string
  data: T
  timestamp: number
}

export interface PageResponse<T> {
  list: T[]
  total: number
  pageNum: number
  pageSize: number
}

export interface PageRequest {
  pageNum?: number
  pageSize?: number
}
