export interface ApiResponse<T = any> {
  code: number
  message: string
  data?: T
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  first: boolean
  last: boolean
  empty: boolean
}

export interface PageRequest {
  page?: number
  size?: number
  sort?: string
  direction?: 'ASC' | 'DESC'
}
