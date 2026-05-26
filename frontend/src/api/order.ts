import { get, post, put } from '@/utils/request'
import type { ApiResponse, PageResponse, Order, OrderRequest } from '@/types'

export const orderApi = {
  getOrders(page = 1, size = 10): Promise<ApiResponse<PageResponse<Order>>> {
    return get('/orders', { page, size })
  },

  getOrderDetail(id: number): Promise<ApiResponse<Order>> {
    return get(`/orders/${id}`)
  },

  createOrder(data: OrderRequest): Promise<ApiResponse<Order>> {
    return post('/orders', data)
  },

  shipOrder(id: number, expressNo: string): Promise<ApiResponse<Order>> {
    return put(`/orders/${id}/ship`, null, { params: { expressNo } })
  },

  confirmOrder(id: number): Promise<ApiResponse<Order>> {
    return put(`/orders/${id}/confirm`)
  },

  cancelOrder(id: number, reason?: string): Promise<ApiResponse<Order>> {
    return put(`/orders/${id}/cancel`, null, { params: { reason } })
  }
}

export default orderApi
