import request from '../utils/request'
import type { ApiResponse, UploadResponse } from '../types'

export function uploadAvatar(file: File): Promise<ApiResponse<UploadResponse>> {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/upload/avatar',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function uploadHabitIcon(file: File): Promise<ApiResponse<UploadResponse>> {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/upload/habit',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function uploadImage(file: File, category: string = 'common'): Promise<ApiResponse<UploadResponse>> {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('category', category)
  return request({
    url: '/upload/image',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function deleteFile(path: string): Promise<ApiResponse<void>> {
  return request({
    url: '/upload',
    method: 'delete',
    params: { path }
  })
}
