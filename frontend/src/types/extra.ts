import type { ApiResponse, PageResponse, User, Product } from './index'

export interface Follow {
  id: number
  followerId: number
  followingId: number
  createTime: string
}

export interface Report {
  id: number
  reporterId: number
  productId: number
  product?: Product
  type: 'FRAUD' | 'INAPPROPRIATE' | 'PROHIBITED' | 'OTHER'
  reason?: string
  status: 'PENDING' | 'PROCESSING' | 'RESOLVED' | 'REJECTED'
  handleResult?: string
  handlerId?: number
  handleTime?: string
  createTime: string
}

export interface Announcement {
  id: number
  title: string
  content: string
  type: 'NORMAL' | 'IMPORTANT' | 'URGENT'
  isTop: boolean
  authorId: number
  author?: User
  viewCount: number
  createTime: string
  updateTime: string
}

export interface Feedback {
  id: number
  userId: number
  user?: User
  type: 'BUG' | 'SUGGESTION' | 'COMPLAINT' | 'OTHER'
  title: string
  content: string
  contactInfo?: string
  images?: string
  status: 'PENDING' | 'PROCESSING' | 'RESOLVED' | 'CLOSED'
  replyContent?: string
  replierId?: number
  replyTime?: string
  createTime: string
}

export interface SearchHistory {
  id: number
  userId: number
  keyword: string
  searchCount: number
  createTime: string
  updateTime: string
}

export interface FollowStats {
  followingCount: number
  followerCount: number
  isFollowing: number
}

export const followApi = {
  getFollowing: async (page = 1, size = 10): Promise<ApiResponse<PageResponse<User>>> => {
    const { get } = await import('@/utils/request')
    return get('/follows/following', { page, size })
  },

  getFollowers: async (page = 1, size = 10): Promise<ApiResponse<PageResponse<User>>> => {
    const { get } = await import('@/utils/request')
    return get('/follows/followers', { page, size })
  },

  getFollowStats: async (userId: number): Promise<ApiResponse<FollowStats>> => {
    const { get } = await import('@/utils/request')
    return get(`/follows/stats/${userId}`)
  },

  follow: async (userId: number): Promise<ApiResponse<void>> => {
    const { post } = await import('@/utils/request')
    return post(`/follows/${userId}`)
  },

  unfollow: async (userId: number): Promise<ApiResponse<void>> => {
    const { del } = await import('@/utils/request')
    return del(`/follows/${userId}`)
  },

  checkFollow: async (userId: number): Promise<ApiResponse<boolean>> => {
    const { get } = await import('@/utils/request')
    return get(`/follows/check/${userId}`)
  }
}

export const reportApi = {
  getMyReports: async (page = 1, size = 10): Promise<ApiResponse<PageResponse<Report>>> => {
    const { get } = await import('@/utils/request')
    return get('/reports/mine', { page, size })
  },

  getReports: async (status?: string, page = 1, size = 10): Promise<ApiResponse<PageResponse<Report>>> => {
    const { get } = await import('@/utils/request')
    return get('/reports', { status, page, size })
  },

  createReport: async (productId: number, type: Report['type'], reason?: string): Promise<ApiResponse<Report>> => {
    const { post } = await import('@/utils/request')
    return post('/reports', null, { params: { productId, type, reason } })
  },

  handleReport: async (id: number, status: Report['status'], result?: string): Promise<ApiResponse<Report>> => {
    const { put } = await import('@/utils/request')
    return put(`/reports/${id}/handle`, null, { params: { status, result } })
  }
}

export const announcementApi = {
  getTop: async (): Promise<ApiResponse<Announcement[]>> => {
    const { get } = await import('@/utils/request')
    return get('/announcements/top')
  },

  getList: async (page = 1, size = 10): Promise<ApiResponse<PageResponse<Announcement>>> => {
    const { get } = await import('@/utils/request')
    return get('/announcements', { page, size })
  },

  getDetail: async (id: number): Promise<ApiResponse<Announcement>> => {
    const { get } = await import('@/utils/request')
    return get(`/announcements/${id}`)
  },

  create: async (data: { title: string; content: string; type?: Announcement['type']; isTop?: boolean }): Promise<ApiResponse<Announcement>> => {
    const { post } = await import('@/utils/request')
    return post('/announcements', null, { params: data })
  },

  update: async (id: number, data: Partial<Announcement>): Promise<ApiResponse<Announcement>> => {
    const { put } = await import('@/utils/request')
    return put(`/announcements/${id}`, null, { params: data })
  },

  delete: async (id: number): Promise<ApiResponse<void>> => {
    const { del } = await import('@/utils/request')
    return del(`/announcements/${id}`)
  }
}

export const feedbackApi = {
  getMine: async (page = 1, size = 10): Promise<ApiResponse<PageResponse<Feedback>>> => {
    const { get } = await import('@/utils/request')
    return get('/feedbacks/mine', { page, size })
  },

  getAll: async (status?: Feedback['status'], page = 1, size = 10): Promise<ApiResponse<PageResponse<Feedback>>> => {
    const { get } = await import('@/utils/request')
    return get('/feedbacks', { status, page, size })
  },

  create: async (data: {
    type: Feedback['type']
    title: string
    content: string
    contactInfo?: string
    images?: string
  }): Promise<ApiResponse<Feedback>> => {
    const { post } = await import('@/utils/request')
    return post('/feedbacks', null, { params: data })
  },

  reply: async (id: number, replyContent: string): Promise<ApiResponse<Feedback>> => {
    const { put } = await import('@/utils/request')
    return put(`/feedbacks/${id}/reply`, null, { params: { replyContent } })
  },

  updateStatus: async (id: number, status: Feedback['status']): Promise<ApiResponse<void>> => {
    const { put } = await import('@/utils/request')
    return put(`/feedbacks/${id}/status`, null, { params: { status } })
  }
}

export const searchHistoryApi = {
  getRecent: async (limit = 10): Promise<ApiResponse<string[]>> => {
    const { get } = await import('@/utils/request')
    return get('/search-history', { limit })
  },

  save: async (keyword: string): Promise<ApiResponse<void>> => {
    const { post } = await import('@/utils/request')
    return post('/search-history', null, { params: { keyword } })
  },

  delete: async (keyword: string): Promise<ApiResponse<void>> => {
    const { del } = await import('@/utils/request')
    return del('/search-history', { params: { keyword } })
  },

  clear: async (): Promise<ApiResponse<void>> => {
    const { del } = await import('@/utils/request')
    return del('/search-history/clear')
  }
}
