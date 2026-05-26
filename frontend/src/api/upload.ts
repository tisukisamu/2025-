import { request } from '@/utils/request'
import type { UploadResponse } from '@/types/response'

export const uploadApi = {
  uploadImage(file: File, type: string = 'voucher') {
    return request.upload<UploadResponse>('/upload/image', file, type)
  },

  uploadAvatar(file: File) {
    return request.upload<UploadResponse>('/upload/avatar', file, 'avatar')
  }
}
