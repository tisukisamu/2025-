import request from '../utils/request'
import type { 
  ApiResponse, 
  CheckRecord, 
  CheckRequest,
  TodayOverview,
  CalendarData,
  PageResponse,
  ReminderItem,
  ReminderSummary
} from '../types'

export function checkIn(data: CheckRequest): Promise<ApiResponse<CheckRecord>> {
  return request({
    url: '/checks',
    method: 'post',
    data
  })
}

export function cancelCheck(habitId: number, date: string): Promise<ApiResponse<void>> {
  return request({
    url: `/checks/${habitId}`,
    method: 'delete',
    params: { date }
  })
}

export function getTodayChecks(): Promise<ApiResponse<CheckRecord[]>> {
  return request({
    url: '/checks/today',
    method: 'get'
  })
}

export function getTodayOverview(): Promise<ApiResponse<TodayOverview>> {
  return request({
    url: '/checks/overview',
    method: 'get'
  })
}

export function getCalendar(month: string, habitId?: number): Promise<ApiResponse<CalendarData>> {
  return request({
    url: '/checks/calendar',
    method: 'get',
    params: { month, habitId }
  })
}

export function getHistory(params?: { 
  habitId?: number
  page?: number
  size?: number
}): Promise<PageResponse<CheckRecord>> {
  return request({
    url: '/checks/history',
    method: 'get',
    params: { 
      page: params?.page ?? 0, 
      size: params?.size ?? 20,
      habitId: params?.habitId
    }
  })
}

export function getTodayReminders(onlyPending = false): Promise<ApiResponse<ReminderItem[]>> {
  return request({
    url: '/reminders/today',
    method: 'get',
    params: { onlyPending }
  })
}

export function getTodayReminderSummary(): Promise<ApiResponse<ReminderSummary>> {
  return request({
    url: '/reminders/today/summary',
    method: 'get'
  })
}
