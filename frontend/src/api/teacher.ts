import request from '../utils/request'
import type { 
  Teacher, 
  TeacherRequest, 
  Course,
  ApiResponse, 
  PageResponse, 
  PageRequest 
} from '../types'

export function getTeachers(params?: PageRequest): Promise<ApiResponse<PageResponse<Teacher>>> {
  return request({
    url: '/teachers',
    method: 'get',
    params
  })
}

export function getTeacherById(id: number): Promise<ApiResponse<Teacher>> {
  return request({
    url: `/teachers/${id}`,
    method: 'get'
  })
}

export function createTeacher(data: TeacherRequest): Promise<ApiResponse<Teacher>> {
  return request({
    url: '/teachers',
    method: 'post',
    data
  })
}

export function updateTeacher(id: number, data: TeacherRequest): Promise<ApiResponse<Teacher>> {
  return request({
    url: `/teachers/${id}`,
    method: 'put',
    data
  })
}

export function deleteTeacher(id: number): Promise<ApiResponse<void>> {
  return request({
    url: `/teachers/${id}`,
    method: 'delete'
  })
}

export function getTeacherCourses(id: number): Promise<ApiResponse<Course[]>> {
  return request({
    url: `/teachers/${id}/courses`,
    method: 'get'
  })
}

export function getTeacherSchedules(id: number): Promise<ApiResponse<any[]>> {
  return request({
    url: `/teachers/${id}/schedules`,
    method: 'get'
  })
}

export function getActiveTeachers(): Promise<ApiResponse<Teacher[]>> {
  return request({
    url: '/teachers/active',
    method: 'get'
  }).then((res: any) => {
    if (res && typeof res.code === 'number') {
      return res as ApiResponse<Teacher[]>
    }
    return {
      code: 200,
      message: 'success',
      data: res as Teacher[]
    }
  })
}

export function searchTeachers(name: string): Promise<ApiResponse<Teacher[]>> {
  return request({
    url: '/teachers/search',
    method: 'get',
    params: { name }
  })
}

export function getCurrentTeacher(): Promise<ApiResponse<Teacher>> {
  return request({
    url: '/teachers/me',
    method: 'get'
  })
}
