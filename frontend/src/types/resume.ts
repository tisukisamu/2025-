export interface Resume {
  id: number
  userId: number
  name: string
  gender?: string
  age?: number
  phone?: string
  email?: string
  education?: string
  experience?: string
  skills?: string
  workExperience?: string
  projectExperience?: string
  educationExperience?: string
  createdAt?: string
  updatedAt?: string
}

export interface ResumeDTO {
  id?: number
  userId?: number
  name: string
  gender?: string
  age?: number
  phone?: string
  email?: string
  education?: string
  experience?: string
  skills?: string
  workExperience?: string
  projectExperience?: string
  educationExperience?: string
}
