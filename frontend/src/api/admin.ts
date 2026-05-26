import { get, put, post, del } from '@/utils/request'
import type { 
  ApiResponse, 
  PageResponse, 
  User, 
  Product, 
  Order, 
  DashboardStats 
} from '@/types'

export const adminApi = {
  getDashboardStats(): Promise<ApiResponse<DashboardStats>> {
    return get('/admin/dashboard')
  },

  getUsers(page = 1, size = 10): Promise<ApiResponse<PageResponse<User>>> {
    return get('/admin/users', { page, size })
  },

  updateUserStatus(id: number, status: User['status']): Promise<ApiResponse<User>> {
    return put(`/admin/users/${id}/status`, null, { params: { status } })
  },

  updateUserRole(id: number, role: User['role']): Promise<ApiResponse<User>> {
    return put(`/admin/users/${id}/role`, null, { params: { role } })
  },

  deleteUser(id: number): Promise<ApiResponse<void>> {
    return del(`/admin/users/${id}`)
  },

  getProducts(page = 1, size = 10, auditStatus?: Product['auditStatus']): Promise<ApiResponse<PageResponse<Product>>> {
    return get('/admin/products', { page, size, auditStatus })
  },

  auditProduct(id: number, auditStatus: Product['auditStatus'], reason?: string): Promise<ApiResponse<Product>> {
    return put(`/admin/products/${id}/audit`, null, { params: { auditStatus, reason } })
  },

  getOrders(page = 1, size = 10, status?: Order['status']): Promise<ApiResponse<PageResponse<Order>>> {
    return get('/admin/orders', { page, size, status })
  }
}

export default adminApi
