import { Enrollment } from './enrollment'

export enum PaymentMethod {
  ALIPAY = 'ALIPAY',
  WECHAT = 'WECHAT',
  CASH = 'CASH',
  CARD = 'CARD'
}

export enum PaymentStatus {
  PENDING = 'PENDING',
  SUCCESS = 'SUCCESS',
  FAILED = 'FAILED',
  REFUNDED = 'REFUNDED'
}

export interface Payment {
  id: number
  enrollment: Enrollment
  amount: number
  paymentMethod?: PaymentMethod
  transactionId?: string
  status: PaymentStatus
  paymentTime?: string
  createdAt: string
  updatedAt: string
}

export interface PaymentRequest {
  enrollmentId?: number
  billId?: number
  amount: number
  paymentMethod: PaymentMethod
}
