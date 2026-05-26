import request from '../utils/request'
import type { 
  Payment, 
  PaymentRequest, 
  Bill, 
  BillRequest,
  ApiResponse, 
  PageResponse, 
  PageRequest 
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

export function getMyPayments(): Promise<ApiResponse<Payment[]>> {
  return request({
    url: '/finance/my/payments',
    method: 'get'
  }).then((res: any) => normalizeApiResponse<Payment[]>(res))
}

export function getMyBills(): Promise<ApiResponse<Bill[]>> {
  return request({
    url: '/finance/my/bills',
    method: 'get'
  }).then((res: any) => normalizeApiResponse<Bill[]>(res))
}

export function createPayment(data: PaymentRequest): Promise<ApiResponse<Payment>> {
  return request({
    url: '/finance/payment',
    method: 'post',
    data
  }).then((res: any) => normalizeApiResponse<Payment>(res))
}

export function getPayments(params?: PageRequest): Promise<ApiResponse<PageResponse<Payment>>> {
  return request({
    url: '/finance/payments',
    method: 'get',
    params
  }).then((res: any) => normalizeApiResponse<PageResponse<Payment>>(res))
}

export function getPaymentById(id: number): Promise<ApiResponse<Payment>> {
  return request({
    url: `/finance/payments/${id}`,
    method: 'get'
  }).then((res: any) => normalizeApiResponse<Payment>(res))
}

export function processPayment(id: number): Promise<ApiResponse<Payment>> {
  return request({
    url: `/finance/payments/${id}/process`,
    method: 'post'
  }).then((res: any) => normalizeApiResponse<Payment>(res))
}

export function refundPayment(id: number): Promise<ApiResponse<Payment>> {
  return request({
    url: `/finance/payments/${id}/refund`,
    method: 'post'
  }).then((res: any) => normalizeApiResponse<Payment>(res))
}

export function getBills(params?: PageRequest): Promise<ApiResponse<PageResponse<Bill>>> {
  return request({
    url: '/finance/bills',
    method: 'get',
    params
  }).then((res: any) => normalizeApiResponse<PageResponse<Bill>>(res))
}

export function createBill(data: BillRequest): Promise<ApiResponse<Bill>> {
  return request({
    url: '/finance/bills',
    method: 'post',
    data
  })
}

export function getStudentBills(studentId: number): Promise<ApiResponse<Bill[]>> {
  return request({
    url: `/finance/bills/student/${studentId}`,
    method: 'get'
  }).then((res: any) => normalizeApiResponse<Bill[]>(res))
}

export function getFinanceStatistics(start: string, end: string): Promise<ApiResponse<any>> {
  return request({
    url: '/finance/statistics',
    method: 'get',
    params: { start, end }
  }).then((res: any) => normalizeApiResponse<any>(res))
}

export function getIncome(start: string, end: string): Promise<ApiResponse<any>> {
  return request({
    url: '/finance/income',
    method: 'get',
    params: { start, end }
  }).then((res: any) => normalizeApiResponse<any>(res))
}
