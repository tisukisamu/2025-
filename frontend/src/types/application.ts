export interface Application {
  id: number
  userId: number
  jobId: number
  resumeId: number
  status: ApplicationStatus
  appliedAt: string
  updatedAt?: string
}

export enum ApplicationStatus {
  PENDING = 'PENDING',
  REVIEWING = 'REVIEWING',
  INTERVIEWED = 'INTERVIEWED',
  ACCEPTED = 'ACCEPTED',
  REJECTED = 'REJECTED'
}

export interface ApplicationDTO {
  id?: number
  userId?: number
  jobId: number
  resumeId: number
  status?: string
}
