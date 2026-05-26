import { get, post, put } from '@/utils/request'
import type { ApiResponse, PageResponse, Review, ReviewRequest } from '@/types'

export const reviewApi = {
  getProductReviews(productId: number, page = 1, size = 10): Promise<ApiResponse<PageResponse<Review>>> {
    return get(`/reviews/product/${productId}`, { page, size })
  },

  getUserReviews(userId: number, page = 1, size = 10): Promise<ApiResponse<PageResponse<Review>>> {
    return get(`/reviews/user/${userId}`, { page, size })
  },

  createReview(data: ReviewRequest): Promise<ApiResponse<Review>> {
    return post('/reviews', data)
  },

  updateReview(id: number, data: ReviewRequest): Promise<ApiResponse<Review>> {
    return put(`/reviews/${id}`, data)
  }
}

export default reviewApi
