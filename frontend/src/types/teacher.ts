export enum TeacherStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE'
}

export interface Teacher {
  id: number
  userId?: number
  name: string
  phone?: string
  email?: string
  subjects?: string
  experience?: number
  bio?: string
  avatar?: string
  status: TeacherStatus
  createdAt: string
  updatedAt: string
}

export interface TeacherRequest {
  userId?: number
  name: string
  phone?: string
  email?: string
  subjects?: string
  experience?: number
  bio?: string
  avatar?: string
  status?: TeacherStatus
}
