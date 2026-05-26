export interface Message {
  id: number
  senderId: number
  receiverId: number
  title: string
  content: string
  type: MessageType
  isRead: boolean
  relatedId?: number
  createdAt?: string
}

export enum MessageType {
  SYSTEM = 'SYSTEM',
  INTERVIEW = 'INTERVIEW',
  APPLICATION = 'APPLICATION',
  ANNOUNCEMENT = 'ANNOUNCEMENT'
}

export interface MessageDTO {
  id?: number
  receiverId: number
  title: string
  content: string
  type: string
  relatedId?: number
}
