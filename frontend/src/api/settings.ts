import request from '../utils/request'
import type { 
  Notification, 
  NotificationRequest,
  Setting,
  SettingRequest,
  ApiResponse, 
  PageResponse, 
  PageRequest 
} from '../types'

export function getSettings(): Promise<ApiResponse<Setting[]>> {
  return request({
    url: '/settings',
    method: 'get'
  })
}

export function updateSetting(key: string, data: SettingRequest): Promise<ApiResponse<Setting>> {
  return request({
    url: `/settings/${key}`,
    method: 'put',
    data
  })
}

export function createNotification(data: NotificationRequest): Promise<ApiResponse<Notification>> {
  return request({
    url: '/settings/notifications',
    method: 'post',
    data
  })
}

export function getNotifications(params?: PageRequest): Promise<ApiResponse<PageResponse<Notification>>> {
  return request({
    url: '/settings/notifications',
    method: 'get',
    params
  })
}
