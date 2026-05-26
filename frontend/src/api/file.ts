import { post } from './request'
import type { ApiResponse } from '@/types/common'

export interface UploadResult {
  path: string
}

export const fileApi = {
  uploadImage: (file: File, category = 'common'): Promise<ApiResponse<UploadResult>> => {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('category', category)
    return post('/files/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  }
}
