import request from '../utils/request'
import type { Result } from '../types'

export function getAdminDashboard(): Promise<Result<any>> {
  return request({
    url: '/admin/dashboard',
    method: 'get'
  })
}

export function getAdminUsers(): Promise<Result<any[]>> {
  return request({
    url: '/admin/users',
    method: 'get'
  })
}

export function getAdminAppointments(): Promise<Result<any[]>> {
  return request({
    url: '/admin/appointments',
    method: 'get'
  })
}
