import { post } from '@/utils/request'
import type { ApiResponse } from '@/types'

export const uploadApi = {
  uploadFile(file: File): Promise<ApiResponse<string>> {
    const formData = new FormData()
    formData.append('file', file)
    return post('/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },

  uploadFiles(files: File[]): Promise<ApiResponse<string[]>> {
    const formData = new FormData()
    files.forEach(file => {
      formData.append('files', file)
    })
    return post('/upload/multiple', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  }
}

export default uploadApi
