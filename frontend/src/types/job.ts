export interface Job {
  id: number
  companyId: number
  title: string
  description?: string
  requirements?: string
  salaryMin?: number
  salaryMax?: number
  location?: string
  jobType?: string
  education?: string
  experience?: string
  coverUrl?: string
  status: JobStatus
  createdAt?: string
  updatedAt?: string
}

export enum JobStatus {
  DRAFT = 'DRAFT',
  ACTIVE = 'ACTIVE',
  CLOSED = 'CLOSED'
}

export interface JobDTO {
  id?: number
  companyId?: number
  title: string
  description?: string
  requirements?: string
  salaryMin?: number
  salaryMax?: number
  location?: string
  jobType?: string
  education?: string
  experience?: string
  coverUrl?: string
  status?: string
}

export interface JobSearchParams {
  title?: string
  location?: string
  education?: string
  experience?: string
  page?: number
  size?: number
}
