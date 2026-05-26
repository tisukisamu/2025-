import request from '../utils/request'
import type { Result } from '../types'
import type { PaymentOrder, PaymentStatus } from '../types/payment'

export function createPayment(appointmentId: number, userId: number, amount: number): Promise<Result<PaymentOrder>> {
  return request({
    url: '/payments/create',
    method: 'post',
    params: { appointmentId, userId, amount }
  })
}

export function getPaymentByOrderNo(orderNo: string): Promise<Result<PaymentOrder>> {
  return request({
    url: `/payments/order/${orderNo}`,
    method: 'get'
  })
}

export function getPaymentByAppointmentId(appointmentId: number): Promise<Result<PaymentOrder>> {
  return request({
    url: `/payments/appointment/${appointmentId}`,
    method: 'get'
  })
}

export function updatePaymentStatus(orderNo: string, status: PaymentStatus, transactionId?: string): Promise<Result<PaymentOrder>> {
  return request({
    url: '/payments/status',
    method: 'put',
    params: { orderNo, status, transactionId }
  })
}

export function refundPayment(orderNo: string): Promise<Result<PaymentOrder>> {
  return request({
    url: `/payments/refund/${orderNo}`,
    method: 'put'
  })
}
