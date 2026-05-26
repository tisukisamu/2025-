import { Course } from './course'
import { Student } from './student'
import { Schedule } from './schedule'

export enum EnrollmentStatus {
  PENDING = 'PENDING',
  CONFIRMED = 'CONFIRMED',
  CANCELLED = 'CANCELLED',
  COMPLETED = 'COMPLETED'
}

export enum PaymentStatus {
  UNPAID = 'UNPAID',
  PAID = 'PAID',
  REFUNDED = 'REFUNDED'
}

export interface Enrollment {
  id: number
  student: Student
  course: Course
  schedule?: Schedule
  enrollmentDate: string
  status: EnrollmentStatus
  paymentStatus: PaymentStatus
  cancelReason?: string
  cancelledAt?: string
  createdAt: string
  updatedAt: string
  billId?: number
}

export interface EnrollmentRequest {
  courseId: number
  scheduleId?: number
  studentId?: number
}
