export interface PaymentOrder {
  id: number
  orderNo: string
  appointmentId: number
  userId: number
  amount: number
  paymentMethod?: string
  transactionId?: string
  status: PaymentStatus
  paidTime?: string
  createdAt: string
  updatedAt: string
}

export type PaymentStatus = 'pending' | 'paid' | 'failed' | 'refunded'
