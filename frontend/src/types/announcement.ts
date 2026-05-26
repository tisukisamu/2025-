export interface Announcement {
  id: number
  title: string
  content: string
  type: AnnouncementType
  status: AnnouncementStatus
  createdBy: number
  createdAt?: string
  updatedAt?: string
}

export enum AnnouncementType {
  NOTICE = 'NOTICE',
  POLICY = 'POLICY',
  UPDATE = 'UPDATE'
}

export enum AnnouncementStatus {
  DRAFT = 'DRAFT',
  PUBLISHED = 'PUBLISHED'
}

export interface AnnouncementDTO {
  id?: number
  title: string
  content: string
  type?: string
  status?: string
}
