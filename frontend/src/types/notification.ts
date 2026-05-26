import { User } from './user'

export enum NotificationType {
  SYSTEM = 'SYSTEM',
  COURSE = 'COURSE',
  PAYMENT = 'PAYMENT',
  GENERAL = 'GENERAL'
}

export enum TargetRole {
  ALL = 'ALL',
  ADMIN = 'ADMIN',
  TEACHER = 'TEACHER',
  STUDENT = 'STUDENT'
}

export interface Notification {
  id: number
  title: string
  content?: string
  type: NotificationType
  targetRole: TargetRole
  isPublished: boolean
  publishedAt?: string
  createdBy?: User
  createdAt: string
  updatedAt: string
}

export interface NotificationRequest {
  title: string
  content?: string
  type?: NotificationType
  targetRole?: TargetRole
  isPublished?: boolean
}
