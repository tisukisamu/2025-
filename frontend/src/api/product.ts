import { get, post, put, del } from '@/utils/request'
import type { 
  ApiResponse, 
  PageResponse, 
  Product, 
  ProductRequest, 
  ProductQueryParams 
} from '@/types'

export const productApi = {
  getProducts(params: ProductQueryParams): Promise<ApiResponse<PageResponse<Product>>> {
    return get('/products', params)
  },

  getProductDetail(id: number): Promise<ApiResponse<Product>> {
    return get(`/products/${id}`)
  },

  createProduct(data: ProductRequest): Promise<ApiResponse<Product>> {
    return post('/products', data)
  },

  updateProduct(id: number, data: ProductRequest): Promise<ApiResponse<Product>> {
    return put(`/products/${id}`, data)
  },

  deleteProduct(id: number): Promise<ApiResponse<void>> {
    return del(`/products/${id}`)
  },

  updateProductStatus(id: number, status: Product['status']): Promise<ApiResponse<void>> {
    return put(`/products/${id}/status`, null, { params: { status } })
  },

  offShelfProduct(id: number): Promise<ApiResponse<void>> {
    return put(`/products/${id}/status`, null, { params: { status: 'OFF_SHELF' } })
  },

  onShelfProduct(id: number): Promise<ApiResponse<void>> {
    return put(`/products/${id}/status`, null, { params: { status: 'ON_SALE' } })
  },

  getMyProducts(page = 1, size = 10): Promise<ApiResponse<PageResponse<Product>>> {
    return get('/products/mine', { page, size })
  }
}

export default productApi
