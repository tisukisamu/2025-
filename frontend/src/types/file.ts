export interface FileRecord {
  id: number
  originalName: string
  storedName: string
  filePath: string
  fileSize: number
  fileType: string
  entityType?: string
  entityId?: number
  uploaderId: number
  createdAt: string
}

export interface UploadResponse {
  url: string
  path: string
}
