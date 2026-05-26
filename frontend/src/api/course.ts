import request from '../utils/request'
import type { 
  Course, 
  CourseRequest, 
  Schedule, 
  Enrollment, 
  EnrollmentRequest,
  ApiResponse, 
  PageResponse, 
  PageRequest 
} from '../types'

export interface CourseListParams extends PageRequest {
  name?: string
  status?: string
}

export function getCourses(params?: CourseListParams): Promise<ApiResponse<PageResponse<Course>>> {
  return request({
    url: '/courses',
    method: 'get',
    params
  })
}

export function getCourseById(id: number): Promise<ApiResponse<Course>> {
  return request({
    url: `/courses/${id}`,
    method: 'get'
  }).then((res: any) => {
    if (res && typeof res.code === 'number') {
      return res as ApiResponse<Course>
    }
    return {
      code: 200,
      message: 'success',
      data: res as Course
    }
  })
}

export function createCourse(data: CourseRequest): Promise<ApiResponse<Course>> {
  return request({
    url: '/courses',
    method: 'post',
    data
  })
}

export function updateCourse(id: number, data: CourseRequest): Promise<ApiResponse<Course>> {
  return request({
    url: `/courses/${id}`,
    method: 'put',
    data
  })
}

export function deleteCourse(id: number): Promise<ApiResponse<void>> {
  return request({
    url: `/courses/${id}`,
    method: 'delete'
  }).then((res: any) => {
    if (res && typeof res.code === 'number') {
      return res as ApiResponse<void>
    }
    return {
      code: 200,
      message: '删除成功'
    } as ApiResponse<void>
  })
}

export function getCourseSchedules(id: number): Promise<ApiResponse<Schedule[]>> {
  return request({
    url: `/courses/${id}/schedules`,
    method: 'get'
  })
}

export function enrollCourse(id: number, data: EnrollmentRequest): Promise<ApiResponse<Enrollment>> {
  return request({
    url: `/courses/${id}/enroll`,
    method: 'post',
    data
  })
}

export function getAvailableCourses(): Promise<ApiResponse<Course[]>> {
  return request({
    url: '/courses/available',
    method: 'get'
  })
}

export function getCoursesByStatus(status: string, params?: PageRequest): Promise<ApiResponse<PageResponse<Course>>> {
  return request({
    url: `/courses/status/${status}`,
    method: 'get',
    params
  })
}
