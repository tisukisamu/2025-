import { get, post } from './request'
import type { 
  InterviewEvaluation, 
  InterviewEvaluationDTO, 
  CandidateFeedback, 
  CompanyRating, 
  CompanyRatingDTO,
  CompanyRatingStatistics 
} from '@/types/evaluation'
import type { ApiResponse } from '@/types/common'

export const evaluationApi = {
  submitEvaluation: (data: InterviewEvaluationDTO): Promise<ApiResponse<InterviewEvaluation>> => {
    return post('/interview-evaluations', data)
  },

  getEvaluation: (interviewId: number): Promise<ApiResponse<InterviewEvaluation>> => {
    return get(`/interview-evaluations/${interviewId}`)
  },

  submitFeedback: (interviewId: number, rating: number, feedback?: string): Promise<ApiResponse<CandidateFeedback>> => {
    const query = new URLSearchParams()
    query.append('interviewId', String(interviewId))
    query.append('rating', String(rating))
    if (feedback) query.append('feedback', feedback)
    return post(`/candidate-feedback?${query.toString()}`)
  },

  getFeedback: (interviewId: number): Promise<ApiResponse<CandidateFeedback>> => {
    return get(`/candidate-feedback/${interviewId}`)
  },

  submitRating: (data: CompanyRatingDTO): Promise<ApiResponse<CompanyRating>> => {
    return post('/company-ratings', data)
  },

  getRatings: (companyId: number): Promise<ApiResponse<CompanyRating[]>> => {
    return get(`/company-ratings/${companyId}`)
  },

  getRatingStatistics: (companyId: number): Promise<ApiResponse<CompanyRatingStatistics>> => {
    return get(`/company-ratings/statistics/${companyId}`)
  },
}
