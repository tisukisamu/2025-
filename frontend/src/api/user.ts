import { get, put, post, del } from '@/utils/request'
import type { 
  ApiResponse, 
  PageResponse, 
  User, 
  Product, 
  Order, 
  Favorite,
  UpdateUserRequest,
  ChangePasswordRequest
} from '@/types'

export const userApi = {
  getCurrentUser(): Promise<ApiResponse<User>> {
    return get('/users/me')
  },

  updateCurrentUser(data: UpdateUserRequest): Promise<ApiResponse<User>> {
    return put('/users/me', data)
  },

  changePassword(data: ChangePasswordRequest): Promise<ApiResponse<void>> {
    return put('/users/me/password', data)
  },

  getMyProducts(page = 1, size = 10): Promise<ApiResponse<PageResponse<Product>>> {
    return get('/users/me/products', { page, size })
  },

  getMyOrders(page = 1, size = 10): Promise<ApiResponse<PageResponse<Order>>> {
    return get('/users/me/orders', { page, size })
  },

  getMyFavorites(page = 1, size = 10): Promise<ApiResponse<PageResponse<Favorite>>> {
    return get('/users/me/favorites', { page, size })
  },

  addFavorite(productId: number): Promise<ApiResponse<void>> {
    return post(`/users/me/favorites/${productId}`)
  },

  removeFavorite(productId: number): Promise<ApiResponse<void>> {
    return del(`/users/me/favorites/${productId}`)
  },

  getUserById(id: number): Promise<ApiResponse<User>> {
    return get(`/users/${id}`)
  }
}

export default userApi
