export interface MemorialAlbum {
  id: number
  petId: number
  userId: number
  title?: string
  description?: string
  photos?: string[]
  isPublic: number
  viewCount: number
  createdAt: string
  updatedAt: string
}

export interface MemorialRequest {
  petId: number
  title?: string
  description?: string
  photos?: string[]
  isPublic?: number
}

export interface MemorialResponse {
  id: number
  petId: number
  userId: number
  title?: string
  description?: string
  photos?: string[]
  isPublic: number
  viewCount: number
  createdAt: string
  updatedAt: string
  petName?: string
  userName?: string
}
