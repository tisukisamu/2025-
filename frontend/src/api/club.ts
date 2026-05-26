import { request } from '@/utils/request'
import type { ClubCreateRequest } from '@/types/request'
import type { Club, User } from '@/types'
import type { PageResult } from '@/types/response'

export const clubApi = {
  create(data: ClubCreateRequest) {
    return request.post<Club>('/club', data)
  },

  update(id: number, data: ClubCreateRequest) {
    return request.put<Club>(`/club/${id}`, data)
  },

  getById(id: number) {
    return request.get<Club>(`/club/${id}`)
  },

  getList(page: number = 0, size: number = 10) {
    return request.get<PageResult<Club>>('/club/list', {
      params: { page, size }
    })
  },

  search(keyword: string, page: number = 0, size: number = 10) {
    return request.get<PageResult<Club>>('/club/search', {
      params: { keyword, page, size }
    })
  },

  getMyClubs() {
    return request.get<Club[]>('/club/my')
  },

  addMember(clubId: number, userId: number, position?: string) {
    return request.post<void>(`/club/${clubId}/member`, null, {
      params: { userId, position }
    })
  },

  removeMember(clubId: number, userId: number) {
    return request.delete<void>(`/club/${clubId}/member/${userId}`)
  },

  updateMemberPosition(clubId: number, userId: number, position: string) {
    return request.put<void>(`/club/${clubId}/member/${userId}/position`, null, {
      params: { position }
    })
  },

  getMembers(clubId: number, page: number = 0, size: number = 10) {
    return request.get<PageResult<User>>(`/club/${clubId}/members`, {
      params: { page, size }
    })
  },

  delete(id: number) {
    return request.delete<void>(`/club/${id}`)
  },

  updateStatus(id: number, status: number) {
    return request.put<Club>(`/club/${id}/status`, null, {
      params: { status }
    })
  }
}
