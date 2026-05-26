export interface InterviewEvaluation {
  id: number
  interviewId: number
  interviewerId: number
  evaluationTemplateId?: number
  scores: Record<string, number>
  comments?: string
  overallScore?: number
  recommendation?: EvaluationRecommendation
  createdAt?: string
}

export enum EvaluationRecommendation {
  STRONGLY_RECOMMEND = 'STRONGLY_RECOMMEND',
  RECOMMEND = 'RECOMMEND',
  NEUTRAL = 'NEUTRAL',
  NOT_RECOMMEND = 'NOT_RECOMMEND'
}

export interface InterviewEvaluationDTO {
  id?: number
  interviewId: number
  scores: Record<string, number>
  comments?: string
  recommendation?: string
}

export interface CandidateFeedback {
  id: number
  interviewId: number
  userId: number
  rating: number
  feedback?: string
  createdAt?: string
}

export interface CompanyRating {
  id: number
  companyId: number
  userId: number
  rating: number
  comment?: string
  createdAt?: string
}

export interface CompanyRatingDTO {
  id?: number
  companyId: number
  rating: number
  comment?: string
}

export interface CompanyRatingStatistics {
  averageRating: number
  totalRatings: number
  distribution: Record<number, number>
}
