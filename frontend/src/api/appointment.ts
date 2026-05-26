import request from '../utils/request'
import type { Result, PageResponse, PageRequest } from '../types'
import type { AppointmentRequest, AppointmentResponse } from '../types/appointment'

export function createAppointment(data: AppointmentRequest): Promise<Result<AppointmentResponse>> {
  return request({
    url: '/appointments',
    method: 'post',
    data
  })
}

export function getAppointmentList(params?: PageRequest & { status?: string }): Promise<Result<PageResponse<AppointmentResponse>>> {
  return request({
    url: '/appointments',
    method: 'get',
    params
  })
}

export function getAppointmentById(id: number): Promise<Result<AppointmentResponse>> {
  return request({
    url: `/appointments/${id}`,
    method: 'get'
  })
}

export function updateAppointmentStatus(id: number, status: string): Promise<Result<void>> {
  return request({
    url: `/appointments/${id}/status`,
    method: 'put',
    params: { status }
  })
}

export function cancelAppointment(id: number): Promise<Result<void>> {
  return request({
    url: `/appointments/${id}/cancel`,
    method: 'put'
  })
}

export function getAppointmentProcess(id: number): Promise<Result<any[]>> {
  return request({
    url: `/appointments/${id}/process`,
    method: 'get'
  })
}

export function getAllAppointments(params?: PageRequest & { status?: string }): Promise<Result<PageResponse<AppointmentResponse>>> {
  return request({
    url: '/admin/appointments',
    method: 'get',
    params
  })
}

export function getServiceAppointments(params?: PageRequest & { status?: string }): Promise<Result<PageResponse<AppointmentResponse>>> {
  return request({
    url: '/service/appointments',
    method: 'get',
    params
  })
}

export function updateServiceAppointmentStatus(id: number, status: string): Promise<Result<void>> {
  return request({
    url: `/service/appointments/${id}/status`,
    method: 'put',
    params: { status }
  })
}
