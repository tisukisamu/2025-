export interface ComplaintSuggestion {
  id: number
  userId: number
  type: ComplaintType
  title: string
  content: string
  status: ComplaintStatus
  handlerId?: number
  handlingResult?: string
  createdAt?: string
  updatedAt?: string
}

export enum ComplaintType {
  COMPLAINT = 'COMPLAINT',
  SUGGESTION = 'SUGGESTION'
}

export enum ComplaintStatus {
  PENDING = 'PENDING',
  PROCESSING = 'PROCESSING',
  RESOLVED = 'RESOLVED',
  REJECTED = 'REJECTED'
}

export interface ComplaintSuggestionDTO {
  id?: number
  type: string
  title: string
  content: string
}
