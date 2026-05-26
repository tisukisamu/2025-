export interface ServicePackage {
  id: number
  name: string
  type: string
  description?: string
  price: number
  originalPrice?: number
  includes?: string
  image?: string
  sortOrder: number
  status: number
  createdAt: string
  updatedAt: string
}

export type ServicePackageRequest = Partial<Omit<ServicePackage, 'id' | 'createdAt' | 'updatedAt'>>
