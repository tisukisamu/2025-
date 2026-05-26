import request from '../utils/request'
import type { 
  ApiResponse, 
  Habit, 
  CreateHabitRequest, 
  UpdateHabitRequest,
  Category,
  CategoryDTO,
  HabitTemplate,
  CreateHabitFromTemplateRequest
} from '../types'

export function getHabits(params?: { 
  categoryId?: number
  status?: 'ACTIVE' | 'PAUSED' | 'DELETED'
}): Promise<ApiResponse<Habit[]>> {
  return request({
    url: '/habits',
    method: 'get',
    params
  })
}

export function getTodayHabits(): Promise<ApiResponse<Habit[]>> {
  return request({
    url: '/habits/today',
    method: 'get'
  })
}

export function getHabitById(id: number): Promise<ApiResponse<Habit>> {
  return request({
    url: `/habits/${id}`,
    method: 'get'
  })
}

export function createHabit(data: CreateHabitRequest): Promise<ApiResponse<Habit>> {
  return request({
    url: '/habits',
    method: 'post',
    data
  })
}

export function updateHabit(id: number, data: UpdateHabitRequest): Promise<ApiResponse<Habit>> {
  return request({
    url: `/habits/${id}`,
    method: 'put',
    data
  })
}

export function deleteHabit(id: number): Promise<ApiResponse<void>> {
  return request({
    url: `/habits/${id}`,
    method: 'delete'
  })
}

export function pauseHabit(id: number): Promise<ApiResponse<void>> {
  return request({
    url: `/habits/${id}/pause`,
    method: 'put'
  })
}

export function resumeHabit(id: number): Promise<ApiResponse<void>> {
  return request({
    url: `/habits/${id}/resume`,
    method: 'put'
  })
}

export function getCategories(): Promise<ApiResponse<Category[]>> {
  return request({
    url: '/habits/categories',
    method: 'get'
  })
}

export function createCategory(data: CategoryDTO): Promise<ApiResponse<Category>> {
  return request({
    url: '/habits/categories',
    method: 'post',
    data
  })
}

export function updateCategory(id: number, data: CategoryDTO): Promise<ApiResponse<Category>> {
  return request({
    url: `/habits/categories/${id}`,
    method: 'put',
    data
  })
}

export function deleteCategory(id: number): Promise<ApiResponse<void>> {
  return request({
    url: `/habits/categories/${id}`,
    method: 'delete'
  })
}

export function getHabitTemplates(keyword?: string): Promise<ApiResponse<HabitTemplate[]>> {
  return getHabitTemplatesWithFilter({ keyword })
}

export function getHabitTemplatesWithFilter(params?: {
  keyword?: string
  categoryName?: string
  repeatType?: 'DAILY' | 'WEEKLY'
}): Promise<ApiResponse<HabitTemplate[]>> {
  return request({
    url: '/templates/habits',
    method: 'get',
    params
  })
}

export function createHabitFromTemplate(
  templateId: number,
  data?: CreateHabitFromTemplateRequest
): Promise<ApiResponse<Habit>> {
  return request({
    url: `/templates/habits/${templateId}/create`,
    method: 'post',
    data: data || {}
  })
}

export function batchUpdateHabitStatus(data: {
  ids: number[]
  action: 'PAUSE' | 'RESUME' | 'DELETE'
}): Promise<ApiResponse<number>> {
  return request({
    url: '/habits/batch/status',
    method: 'put',
    data
  })
}
