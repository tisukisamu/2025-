import request from '../utils/request'
import type { Result } from '../types'
import type { FileRecord, UploadResponse } from '../types/file'

export function uploadFile(
  file: File,
  entityType?: string,
  entityId?: number
): Promise<Result<UploadResponse>> {
  const formData = new FormData()
  formData.append('file', file)
  if (entityType) formData.append('entityType', entityType)
  if (entityId) formData.append('entityId', String(entityId))
  
  return request({
    url: '/files/upload',
    method: 'post',
    data: formData
  })
}

export function deleteFile(filePath: string): Promise<Result<void>> {
  return request({
    url: '/files',
    method: 'delete',
    params: { filePath }
  })
}

export function getFilesByEntity(entityType: string, entityId: number): Promise<Result<FileRecord[]>> {
  return request({
    url: `/files/entity/${entityType}/${entityId}`,
    method: 'get'
  })
}

export function getFileUrl(filePath: string): string {
  return `/api/files/${filePath}`
}
