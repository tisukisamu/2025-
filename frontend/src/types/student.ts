export enum StudentStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE'
}

export enum StudentGender {
  MALE = 'MALE',
  FEMALE = 'FEMALE',
  OTHER = 'OTHER'
}

export interface Student {
  id: number
  userId?: number
  name: string
  phone?: string
  email?: string
  gender?: StudentGender
  birthDate?: string
  emergencyContact?: string
  emergencyPhone?: string
  avatar?: string
  status: StudentStatus
  createdAt: string
  updatedAt: string
}

export interface StudentRequest {
  userId?: number
  name: string
  phone?: string
  email?: string
  gender?: StudentGender
  birthDate?: string
  emergencyContact?: string
  emergencyPhone?: string
  avatar?: string
  status?: StudentStatus
}
