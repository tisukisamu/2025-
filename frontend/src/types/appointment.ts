export interface Appointment {
  id: number
  orderNo: string
  userId: number
  petId: number
  packageId: number
  appointmentTime: string
  contactName: string
  contactPhone: string
  address?: string
  remark?: string
  status: AppointmentStatus
  operatorId?: number
  createdAt: string
  updatedAt: string
}

export type AppointmentStatus = 'pending' | 'confirmed' | 'processing' | 'completed' | 'cancelled'

export interface AppointmentRequest {
  petId: number
  packageId: number
  appointmentTime: string
  contactName: string
  contactPhone: string
  address?: string
  remark?: string
}

export interface AppointmentResponse {
  id: number
  orderNo: string
  userId: number
  petId: number
  packageId: number
  appointmentTime: string
  contactName: string
  contactPhone: string
  address?: string
  remark?: string
  status: string
  operatorId?: number
  createdAt: string
  updatedAt: string
  petName?: string
  petType?: string
  petPhoto?: string
  packageName?: string
  packagePrice?: number
}
