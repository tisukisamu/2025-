import { get } from '@/utils/request'
import type { ApiResponse, Category } from '@/types'

export const categoryApi = {
  getAllCategories(): Promise<ApiResponse<Category[]>> {
    return get('/categories')
  },

  getRootCategories(): Promise<ApiResponse<Category[]>> {
    return get('/categories/root')
  },

  getSubCategories(parentId: number): Promise<ApiResponse<Category[]>> {
    return get(`/categories/${parentId}/children`)
  },

  getCategoryById(id: number): Promise<ApiResponse<Category>> {
    return get(`/categories/${id}`)
  }
}

export default categoryApi
