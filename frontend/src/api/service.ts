import request from '../utils/request'
import type { Result } from '../types'
import type { ServicePackage, ServicePackageRequest } from '../types/service'

export function getServiceList(): Promise<Result<ServicePackage[]>> {
  return request({
    url: '/services',
    method: 'get'
  })
}

export function getServicesByType(type: string): Promise<Result<ServicePackage[]>> {
  return request({
    url: `/services/type/${type}`,
    method: 'get'
  })
}

export function getServiceById(id: number): Promise<Result<ServicePackage>> {
  return request({
    url: `/services/${id}`,
    method: 'get'
  })
}

export function createService(data: ServicePackageRequest): Promise<Result<ServicePackage>> {
  return request({
    url: '/services',
    method: 'post',
    data
  })
}

export function updateService(id: number, data: ServicePackageRequest): Promise<Result<ServicePackage>> {
  return request({
    url: `/services/${id}`,
    method: 'put',
    data
  })
}

export function deleteService(id: number): Promise<Result<void>> {
  return request({
    url: `/services/${id}`,
    method: 'delete'
  })
}

export function updateServiceStatus(id: number, status: number): Promise<Result<void>> {
  return request({
    url: `/services/${id}/status`,
    method: 'put',
    params: { status }
  })
}
