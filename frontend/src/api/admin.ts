import { get, put } from './request'
import type { User } from '@/types/user'
import type { Company } from '@/types/company'
import type { Announcement } from '@/types/announcement'
import type { ComplaintSuggestion } from '@/types/complaint'
import type { ApiResponse } from '@/types/common'

export const adminApi = {
  getDashboard: (): Promise<ApiResponse<any>> => {
    return get('/admin/dashboard')
  },

  getUsers: (): Promise<ApiResponse<User[]>> => {
    return get('/admin/users')
  },

  updateUserStatus: (id: number, status: 'ACTIVE' | 'DISABLED'): Promise<ApiResponse<void>> => {
    return put(`/admin/users/${id}/status?status=${status}`)
  },

  getJobs: (): Promise<ApiResponse<any[]>> => {
    return get('/admin/jobs')
  },

  approveJob: (id: number): Promise<ApiResponse<void>> => {
    return put(`/admin/jobs/${id}/approve`)
  },

  getAnnouncements: (): Promise<ApiResponse<Announcement[]>> => {
    return get('/announcements/admin/all')
  },

  getComplaints: (): Promise<ApiResponse<ComplaintSuggestion[]>> => {
    return get('/admin/complaints')
  },
}
