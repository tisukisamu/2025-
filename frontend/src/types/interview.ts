export interface Interview {
  id: number
  applicationId: number
  interviewTime: string
  location?: string
  interviewer?: string
  result?: InterviewResult
  feedback?: string
  createdAt?: string
  updatedAt?: string
}

export enum InterviewResult {
  PENDING = 'PENDING',
  PASSED = 'PASSED',
  FAILED = 'FAILED'
}

export interface InterviewDTO {
  id?: number
  applicationId: number
  interviewTime: string
  location: string
  interviewer?: string
  result?: string
  feedback?: string
}
