import { get, post, put } from '@/utils/request'
import type { ApiResponse, Message, MessageRequest } from '@/types'

export const messageApi = {
  getMessages(page = 1, size = 20): Promise<ApiResponse<any>> {
    return get('/messages', { page, size })
  },

  getConversation(userId: number): Promise<ApiResponse<Message[]>> {
    return get(`/messages/${userId}`)
  },

  sendMessage(data: MessageRequest): Promise<ApiResponse<Message>> {
    return post('/messages', data)
  },

  markAsRead(userId: number): Promise<ApiResponse<void>> {
    return put(`/messages/read/${userId}`)
  },

  getUnreadCount(): Promise<ApiResponse<number>> {
    return get('/messages/unread-count')
  }
}

export default messageApi
