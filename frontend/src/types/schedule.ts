import { Course } from './course'
import { Teacher } from './teacher'

export enum ScheduleStatus {
  SCHEDULED = 'SCHEDULED',
  CANCELLED = 'CANCELLED',
  COMPLETED = 'COMPLETED'
}

export interface Schedule {
  id: number
  course: Course
  teacher: Teacher
  startTime: string
  endTime: string
  location?: string
  room?: string
  isRecurring: boolean
  recurrencePattern?: string
  status: ScheduleStatus
  createdAt: string
  updatedAt: string
}

export interface ScheduleRequest {
  courseId: number
  teacherId: number
  startTime: string
  endTime: string
  location?: string
  room?: string
  isRecurring?: boolean
  recurrencePattern?: string
}
