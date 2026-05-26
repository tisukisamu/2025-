import { get, post, put, del } from './request'
import type { Message, MessageDTO } from '@/types/message'
import type { ApiResponse, PageResponse } from '@/types/common'

export const messageApi = {
  getList: (page = 0, size = 10): Promise<ApiResponse<PageResponse<Message>>> => {
    return get(`/messages?page=${page}&size=${size}`)
  },

  getUnread: (): Promise<ApiResponse<Message[]>> => {
    return get('/messages/unread')
  },

  getUnreadCount: (): Promise<ApiResponse<number>> => {
    return get('/messages/unread-count')
  },

  getById: (id: number): Promise<ApiResponse<Message>> => {
    return get(`/messages/${id}`)
  },

  markRead: (id: number): Promise<ApiResponse<void>> => {
    return put(`/messages/${id}/read`)
  },

  markAllRead: (): Promise<ApiResponse<void>> => {
    return put('/messages/read-all')
  },

  delete: (id: number): Promise<ApiResponse<void>> => {
    return del(`/messages/${id}`)
  },

  send: (data: MessageDTO): Promise<ApiResponse<Message>> => {
    return post('/messages', data)
  },
}
