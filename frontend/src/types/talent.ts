export interface TalentPool {
  id: number
  companyId: number
  resumeId: number
  userId: number
  tags?: string
  groupId?: number
  status: TalentStatus
  notes?: string
  matchScore?: number
  addedAt?: string
  updatedAt?: string
}

export enum TalentStatus {
  COLLECTED = 'COLLECTED',
  CONTACTED = 'CONTACTED',
  INTERVIEWED = 'INTERVIEWED',
  BLACKLISTED = 'BLACKLISTED'
}

export interface TalentGroup {
  id: number
  companyId: number
  name: string
  description?: string
  color?: string
  createdAt?: string
  updatedAt?: string
}
