import request from '@/utils/request'

export const buyRequestApi = {
  getBuyRequests: (params: { status?: string; page?: number; size?: number }) => 
    request.get('/buy-requests', { params }),
  
  getBuyRequestById: (id: number) => 
    request.get(`/buy-requests/${id}`),
  
  getMyBuyRequests: (params: { page?: number; size?: number }) => 
    request.get('/buy-requests/mine', { params }),
  
  createBuyRequest: (data: any) => 
    request.post('/buy-requests', null, { params: data }),
  
  updateBuyRequest: (id: number, data: any) => 
    request.put(`/buy-requests/${id}`, null, { params: data }),
  
  closeBuyRequest: (id: number) => 
    request.put(`/buy-requests/${id}/close`),
  
  getResponses: (id: number, params: { page?: number; size?: number }) => 
    request.get(`/buy-requests/${id}/responses`, { params }),
  
  createResponse: (id: number, data: any) => 
    request.post(`/buy-requests/${id}/responses`, null, { params: data }),
  
  acceptResponse: (responseId: number) => 
    request.put(`/buy-requests/responses/${responseId}/accept`),
  
  rejectResponse: (responseId: number) => 
    request.put(`/buy-requests/responses/${responseId}/reject`),
}

export const topicApi = {
  getTopics: (params: { category?: string; page?: number; size?: number }) => 
    request.get('/topics', { params }),
  
  getHotTopics: (params: { page?: number; size?: number }) => 
    request.get('/topics/hot', { params }),
  
  getTopicById: (id: number) => 
    request.get(`/topics/${id}`),
  
  getMyTopics: (params: { page?: number; size?: number }) => 
    request.get('/topics/mine', { params }),
  
  createTopic: (data: any) => 
    request.post('/topics', null, { params: data }),
  
  updateTopic: (id: number, data: any) => 
    request.put(`/topics/${id}`, null, { params: data }),
  
  deleteTopic: (id: number) => 
    request.delete(`/topics/${id}`),
  
  likeTopic: (id: number) => 
    request.post(`/topics/${id}/like`),
  
  unlikeTopic: (id: number) => 
    request.delete(`/topics/${id}/like`),
  
  getComments: (id: number, params: { page?: number; size?: number }) => 
    request.get(`/topics/${id}/comments`, { params }),
  
  createComment: (id: number, data: any) => 
    request.post(`/topics/${id}/comments`, null, { params: data }),
  
  deleteComment: (commentId: number) => 
    request.delete(`/topics/comments/${commentId}`),
  
  likeComment: (commentId: number) => 
    request.post(`/topics/comments/${commentId}/like`),
}

export const creditApi = {
  getCreditInfo: () => 
    request.get('/credit/info'),
  
  getCreditHistory: (params: { page?: number; size?: number }) => 
    request.get('/credit/history', { params }),
  
  dailyLogin: () => 
    request.post('/credit/daily-login'),
}
