import { get, put } from '@/utils/request'
import type { ApiResponse, Notification } from '@/types'

export const notificationApi = {
  getNotifications(page = 1, size = 10): Promise<ApiResponse<any>> {
    return get('/notifications', { page, size })
  },

  getUnreadNotifications(): Promise<ApiResponse<Notification[]>> {
    return get('/notifications/unread')
  },

  getUnreadCount(): Promise<ApiResponse<number>> {
    return get('/notifications/unread-count')
  },

  markAsRead(id: number): Promise<ApiResponse<void>> {
    return put(`/notifications/${id}/read`)
  },

  markAllAsRead(): Promise<ApiResponse<void>> {
    return put('/notifications/read-all')
  }
}

export default notificationApi
