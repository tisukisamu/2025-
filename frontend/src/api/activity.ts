import { request } from '@/utils/request'
import type { ActivityCreateRequest } from '@/types/request'
import type { Activity } from '@/types'
import type { PageResult } from '@/types/response'

export const activityApi = {
  create(data: ActivityCreateRequest) {
    return request.post<Activity>('/activity', data)
  },

  update(id: number, data: ActivityCreateRequest) {
    return request.put<Activity>(`/activity/${id}`, data)
  },

  publish(id: number) {
    return request.post<Activity>(`/activity/${id}/publish`)
  },

  submit(id: number) {
    return request.post<Activity>(`/activity/${id}/submit`)
  },

  approve(id: number) {
    return request.post<Activity>(`/activity/${id}/approve`)
  },

  reject(id: number) {
    return request.post<Activity>(`/activity/${id}/reject`)
  },

  getById(id: number) {
    return request.get<Activity>(`/activity/${id}`)
  },

  getList(params?: {
    clubId?: number
    page?: number
    size?: number
  }) {
    return request.get<PageResult<Activity>>('/activity/list', {
      params: { ...params }
    })
  },

  search(keyword: string, page: number = 0, size: number = 10) {
    return request.get<PageResult<Activity>>('/activity/search', {
      params: { keyword, page, size }
    })
  },

  getReviewList(page: number = 0, size: number = 10) {
    return request.get<PageResult<Activity>>('/activity/review-list', {
      params: { page, size }
    })
  },

  getMyCreated(page: number = 0, size: number = 10) {
    return request.get<PageResult<Activity>>('/activity/my-created', {
      params: { page, size }
    })
  },

  getMySignups() {
    return request.get<Activity[]>('/activity/my-signups')
  },

  signup(id: number) {
    return request.post<void>(`/activity/${id}/signup`)
  },

  cancelSignup(id: number) {
    return request.delete<void>(`/activity/${id}/signup`)
  },

  delete(id: number) {
    return request.delete<void>(`/activity/${id}`)
  }
}
