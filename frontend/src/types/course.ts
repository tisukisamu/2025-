import { Teacher } from './teacher'

export enum CourseLevel {
  BEGINNER = 'BEGINNER',
  INTERMEDIATE = 'INTERMEDIATE',
  ADVANCED = 'ADVANCED'
}

export enum CourseStatus {
  DRAFT = 'DRAFT',
  PUBLISHED = 'PUBLISHED',
  CLOSED = 'CLOSED'
}

export interface Course {
  id: number
  name: string
  description?: string
  teacher?: Teacher
  category?: string
  level?: CourseLevel
  duration?: number
  price: number
  capacity: number
  enrolledCount: number
  image?: string
  status: CourseStatus
  createdAt: string
  updatedAt: string
}

export interface CourseRequest {
  name: string
  description?: string
  teacherId?: number
  category?: string
  level?: CourseLevel
  duration?: number
  price: number
  capacity?: number
  image?: string
  status?: CourseStatus
}
