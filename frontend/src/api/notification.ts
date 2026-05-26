import { request } from '@/utils/request'
import type { Notification } from '@/types'
import type { PageResult } from '@/types/response'

export const notificationApi = {
  getList(params?: {
    type?: string
    page?: number
    size?: number
  }) {
    return request.get<PageResult<Notification>>('/notification/list', {
      params: { ...params }
    })
  },

  getById(id: number) {
    return request.get<Notification>(`/notification/${id}`)
  },

  getUnreadCount() {
    return request.get<number>('/notification/unread-count')
  },

  markAsRead(id: number) {
    return request.put<void>(`/notification/${id}/read`)
  },

  markAllAsRead() {
    return request.put<void>('/notification/read-all')
  }
}
