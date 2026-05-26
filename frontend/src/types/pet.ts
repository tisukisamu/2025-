export interface Pet {
  id: number
  userId: number
  name: string
  type: string
  breed?: string
  gender?: string
  birthday?: string
  passDate?: string
  photo?: string
  color?: string
  weight?: number
  description?: string
  memorialText?: string
  createdAt: string
  updatedAt: string
}

export interface PetRequest {
  name: string
  type: string
  breed?: string
  gender?: string
  birthday?: string
  passDate?: string
  photo?: string
  color?: string
  weight?: number
  description?: string
  memorialText?: string
}

export interface PetResponse {
  id: number
  userId: number
  name: string
  type: string
  breed?: string
  gender?: string
  birthday?: string
  passDate?: string
  photo?: string
  color?: string
  weight?: number
  description?: string
  memorialText?: string
  createdAt: string
  updatedAt: string
}
