import { Student } from './student'
import { Payment } from './payment'

export enum BillType {
  TUITION = 'TUITION',
  REGISTRATION = 'REGISTRATION',
  OTHER = 'OTHER'
}

export enum BillStatus {
  UNPAID = 'UNPAID',
  PAID = 'PAID',
  OVERDUE = 'OVERDUE'
}

export interface Bill {
  id: number
  student: Student
  enrollmentId?: number
  billType: BillType
  amount: number
  description?: string
  dueDate?: string
  status: BillStatus
  payment?: Payment
  createdAt: string
  updatedAt: string
}

export interface BillRequest {
  studentId: number
  billType: BillType
  amount: number
  description?: string
  dueDate?: string
}
