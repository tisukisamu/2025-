export type ProcessStage = 
  | 'confirmed' 
  | 'pickup' 
  | 'farewell' 
  | 'cremation' 
  | 'processing' 
  | 'memorial' 
  | 'completed'

export type ProcessStatus = 'pending' | 'processing' | 'completed'

export interface ProcessStageData {
  id: number
  appointmentId: number
  stage: ProcessStage
  status: ProcessStatus
  operatorId?: number
  description?: string
  photos?: string[]
  videos?: string[]
  startTime?: string
  endTime?: string
  createdAt: string
  updatedAt: string
}

export interface ProcessUpdateRequest {
  stage: ProcessStage
  status: ProcessStatus
  description?: string
  photos?: string[]
  videos?: string[]
}

export type ProcessResponse = ProcessStageData
