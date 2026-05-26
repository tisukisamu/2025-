import { del, get, post } from './request'
import type { Job } from '@/types/job'
import type { ApiResponse } from '@/types/common'

export const favoriteApi = {
  getMyJobs: (): Promise<ApiResponse<Job[]>> => {
    return get('/favorites/jobs')
  },

  getMyJobIds: (): Promise<ApiResponse<number[]>> => {
    return get('/favorites/jobs/ids')
  },

  add: (jobId: number): Promise<ApiResponse<void>> => {
    return post(`/favorites/jobs/${jobId}`)
  },

  remove: (jobId: number): Promise<ApiResponse<void>> => {
    return del(`/favorites/jobs/${jobId}`)
  },
}
