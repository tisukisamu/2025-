import request from '../utils/request'
import type {
  ApiResponse,
  CommunityComment,
  CommunityPost,
  CreateCommunityCommentRequest,
  CreateCommunityPostRequest
} from '../types'

export function getCommunityPosts(): Promise<ApiResponse<CommunityPost[]>> {
  return request({
    url: '/community/posts',
    method: 'get'
  })
}

export function createCommunityPost(data: CreateCommunityPostRequest): Promise<ApiResponse<CommunityPost>> {
  return request({
    url: '/community/posts',
    method: 'post',
    data
  })
}

export function deleteCommunityPost(postId: number): Promise<ApiResponse<void>> {
  return request({
    url: `/community/posts/${postId}`,
    method: 'delete'
  })
}

export function getPostComments(postId: number): Promise<ApiResponse<CommunityComment[]>> {
  return request({
    url: `/community/posts/${postId}/comments`,
    method: 'get'
  })
}

export function createPostComment(postId: number, data: CreateCommunityCommentRequest): Promise<ApiResponse<CommunityComment>> {
  return request({
    url: `/community/posts/${postId}/comments`,
    method: 'post',
    data
  })
}

export function deletePostComment(commentId: number): Promise<ApiResponse<void>> {
  return request({
    url: `/community/comments/${commentId}`,
    method: 'delete'
  })
}
