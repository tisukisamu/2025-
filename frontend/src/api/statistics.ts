import { get } from './request'
import type { CompanyOverview, RecruitmentTrend, TalentAnalysis } from '@/types/common'
import type { ApiResponse } from '@/types/common'

export const statisticsApi = {
  getOverview: (): Promise<ApiResponse<CompanyOverview>> => {
    return get('/statistics/overview')
  },

  getCompanyOverview: (companyId: number): Promise<ApiResponse<CompanyOverview>> => {
    return get(`/statistics/company/${companyId}`)
  },

  getRecruitmentTrend: (companyId: number, start: string, end: string): Promise<ApiResponse<RecruitmentTrend>> => {
    return get(`/statistics/recruitment?companyId=${companyId}&start=${start}&end=${end}`)
  },

  getTalentAnalysis: (companyId: number): Promise<ApiResponse<TalentAnalysis>> => {
    return get(`/statistics/talent?companyId=${companyId}`)
  },

  getAdminSystemStats: (): Promise<ApiResponse<CompanyOverview>> => {
    return get('/statistics/admin/system')
  },
}
