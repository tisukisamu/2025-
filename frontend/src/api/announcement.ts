import { get, post, put, del } from './request'
import type { Announcement, AnnouncementDTO } from '@/types/announcement'
import type { ApiResponse } from '@/types/common'

export const announcementApi = {
  getPublished: (): Promise<ApiResponse<Announcement[]>> => {
    return get('/announcements')
  },

  getById: (id: number): Promise<ApiResponse<Announcement>> => {
    return get(`/announcements/${id}`)
  },

  getAll: (): Promise<ApiResponse<Announcement[]>> => {
    return get('/announcements/admin/all')
  },

  create: (data: AnnouncementDTO): Promise<ApiResponse<Announcement>> => {
    return post('/announcements', data)
  },

  update: (id: number, data: AnnouncementDTO): Promise<ApiResponse<Announcement>> => {
    return put(`/announcements/${id}`, data)
  },

  publish: (id: number): Promise<ApiResponse<void>> => {
    return put(`/announcements/${id}/publish`)
  },

  delete: (id: number): Promise<ApiResponse<void>> => {
    return del(`/announcements/${id}`)
  },
}
