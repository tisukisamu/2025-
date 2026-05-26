import request from '../utils/request'
import type { 
  Student, 
  StudentRequest, 
  Enrollment,
  Course,
  ApiResponse, 
  PageResponse, 
  PageRequest 
} from '../types'

export interface StudentCourseEnrollment {
  enrollmentId: number
  enrollmentDate: string
  status: 'PENDING' | 'CONFIRMED' | 'CANCELLED' | 'COMPLETED'
  paymentStatus: 'UNPAID' | 'PAID' | 'REFUNDED'
  course: Course & { teacherName?: string }
}

export function getStudents(params?: PageRequest): Promise<ApiResponse<PageResponse<Student>>> {
  return request({
    url: '/students',
    method: 'get',
    params
  })
}

export function getStudentById(id: number): Promise<ApiResponse<Student>> {
  return request({
    url: `/students/${id}`,
    method: 'get'
  })
}

export function createStudent(data: StudentRequest): Promise<ApiResponse<Student>> {
  return request({
    url: '/students',
    method: 'post',
    data
  })
}

export function updateStudent(id: number, data: StudentRequest): Promise<ApiResponse<Student>> {
  return request({
    url: `/students/${id}`,
    method: 'put',
    data
  })
}

export function deleteStudent(id: number): Promise<ApiResponse<void>> {
  return request({
    url: `/students/${id}`,
    method: 'delete'
  })
}

export function getStudentEnrollments(id: number): Promise<ApiResponse<Enrollment[]>> {
  return request({
    url: `/students/${id}/enrollments`,
    method: 'get'
  })
}

export function getStudentCourses(id: number): Promise<ApiResponse<any[]>> {
  return request({
    url: `/students/${id}/courses`,
    method: 'get'
  })
}

export function getMyCourses(): Promise<ApiResponse<StudentCourseEnrollment[]>> {
  return request({
    url: '/students/me/courses',
    method: 'get'
  })
}

export function cancelEnrollment(enrollmentId: number, reason?: string): Promise<ApiResponse<void>> {
  return request({
    url: `/students/enrollments/${enrollmentId}`,
    method: 'delete',
    params: { reason }
  })
}

export function getActiveStudents(): Promise<ApiResponse<Student[]>> {
  return request({
    url: '/students/active',
    method: 'get'
  })
}

export function searchStudents(name: string): Promise<ApiResponse<Student[]>> {
  return request({
    url: '/students/search',
    method: 'get',
    params: { name }
  })
}
