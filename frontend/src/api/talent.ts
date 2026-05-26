import { get, post, put, del } from './request'
import type { TalentPool, TalentGroup } from '@/types/talent'
import type { ApiResponse } from '@/types/common'

export const talentApi = {
  getPool: (): Promise<ApiResponse<TalentPool[]>> => {
    return get('/talent-pool')
  },

  addToPool: (resumeId: number, userId: number): Promise<ApiResponse<TalentPool>> => {
    return post(`/talent-pool?resumeId=${resumeId}&userId=${userId}`)
  },

  updateTags: (id: number, tags: string): Promise<ApiResponse<TalentPool>> => {
    return put(`/talent-pool/${id}/tags?tags=${encodeURIComponent(tags)}`)
  },

  moveToGroup: (id: number, groupId: number): Promise<ApiResponse<TalentPool>> => {
    return put(`/talent-pool/${id}/group?groupId=${groupId}`)
  },

  removeFromPool: (id: number): Promise<ApiResponse<void>> => {
    return del(`/talent-pool/${id}`)
  },

  getGroups: (): Promise<ApiResponse<TalentGroup[]>> => {
    return get('/talent-groups')
  },

  createGroup: (name: string, description?: string, color?: string): Promise<ApiResponse<TalentGroup>> => {
    const query = new URLSearchParams()
    query.append('name', name)
    if (description) query.append('description', description)
    if (color) query.append('color', color)
    return post(`/talent-groups?${query.toString()}`)
  },

  updateGroup: (id: number, name: string, description?: string, color?: string): Promise<ApiResponse<TalentGroup>> => {
    const query = new URLSearchParams()
    query.append('name', name)
    if (description) query.append('description', description)
    if (color) query.append('color', color)
    return put(`/talent-groups/${id}?${query.toString()}`)
  },

  deleteGroup: (id: number): Promise<ApiResponse<void>> => {
    return del(`/talent-groups/${id}`)
  },
}
