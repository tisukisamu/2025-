export interface Company {
  id: number
  name: string
  description?: string
  industry?: string
  scale?: string
  address?: string
  contactPerson?: string
  contactPhone?: string
  contactEmail?: string
  logoUrl?: string
  userId?: number
  status: CompanyStatus
  createdAt?: string
  updatedAt?: string
}

export enum CompanyStatus {
  PENDING = 'PENDING',
  APPROVED = 'APPROVED',
  REJECTED = 'REJECTED'
}

export interface CompanyDTO {
  id?: number
  name: string
  description?: string
  industry?: string
  scale?: string
  address?: string
  contactPerson?: string
  contactPhone?: string
  contactEmail?: string
  logoUrl?: string
}
