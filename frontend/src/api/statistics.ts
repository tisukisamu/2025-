import request from '../utils/request'
import type { 
  StatisticsDTO,
  ApiResponse
} from '../types'

function normalizeApiResponse<T>(res: any): ApiResponse<T> {
  if (res && typeof res.code === 'number') {
    return res as ApiResponse<T>
  }
  return {
    code: 200,
    message: 'success',
    data: res as T
  }
}

export function getCourseStatistics(): Promise<ApiResponse<StatisticsDTO['courseStatistics']>> {
  return request({
    url: '/statistics/courses',
    method: 'get'
  }).then((res: any) => normalizeApiResponse<StatisticsDTO['courseStatistics']>(res))
}

export function getFinanceStatistics(start?: string, end?: string): Promise<ApiResponse<StatisticsDTO['financeStatistics']>> {
  return request({
    url: '/statistics/finance',
    method: 'get',
    params: { start, end }
  }).then((res: any) => normalizeApiResponse<StatisticsDTO['financeStatistics']>(res))
}

export function getStudentStatistics(): Promise<ApiResponse<StatisticsDTO['studentStatistics']>> {
  return request({
    url: '/statistics/students',
    method: 'get'
  }).then((res: any) => normalizeApiResponse<StatisticsDTO['studentStatistics']>(res))
}

export function getDashboardStatistics(): Promise<ApiResponse<any>> {
  return request({
    url: '/statistics/dashboard',
    method: 'get'
  }).then((res: any) => normalizeApiResponse<any>(res))
}
