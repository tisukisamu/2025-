export interface Message {
  id: number
  albumId: number
  userId?: number
  authorName: string
  content: string
  createdAt: string
}

export interface MessageRequest {
  albumId: number
  userId?: number
  authorName: string
  content: string
}
