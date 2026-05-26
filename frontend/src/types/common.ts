export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}

export interface StatisticsOverview {
  totalUsers: number
  totalCompanies: number
  totalJobs: number
  activeJobs: number
}

export interface CompanyOverview {
  totalJobs: number
  activeJobs: number
  totalApplications: number
  pendingApplications: number
  interviewedApplications: number
  hiredApplications: number
}

export interface RecruitmentTrend {
  dates: string[]
  counts: number[]
}

export interface TalentAnalysis {
  totalApplications: number
  educationDistribution: Record<string, number>
}
