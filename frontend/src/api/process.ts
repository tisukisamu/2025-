import request from '../utils/request'
import type { Result } from '../types'
import type { ProcessStageData, ProcessUpdateRequest } from '../types/process'

export function getProcessByAppointment(appointmentId: number): Promise<Result<ProcessStageData[]>> {
  return request({
    url: `/process/appointment/${appointmentId}`,
    method: 'get'
  })
}

export function updateProcess(appointmentId: number, data: ProcessUpdateRequest): Promise<Result<ProcessStageData>> {
  return request({
    url: `/process/appointment/${appointmentId}`,
    method: 'put',
    data
  })
}
