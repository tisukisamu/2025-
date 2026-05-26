import { request } from '@/utils/request'
import type { FundApplyRequest } from '@/types/request'
import type { FundApply, FundFlow } from '@/types'
import type { PageResult } from '@/types/response'

export const fundApi = {
  createApply(data: FundApplyRequest) {
    return request.post<FundApply>('/fund/apply', data)
  },

  updateApply(id: number, data: FundApplyRequest) {
    return request.put<FundApply>(`/fund/${id}`, data)
  },

  getApplyById(id: number) {
    return request.get<FundApply>(`/fund/${id}`)
  },

  getApplyList(params: {
    clubId?: number
    status?: string
    page?: number
    size?: number
  }) {
    return request.get<PageResult<FundApply>>('/fund/list', {
      params: { ...params }
    })
  },

  getMyApplies(page: number = 0, size: number = 10) {
    return request.get<PageResult<FundApply>>('/fund/my', {
      params: { page, size }
    })
  },

  deleteApply(id: number) {
    return request.delete<void>(`/fund/${id}`)
  },

  getFlowList(params: {
    clubId: number
    startTime?: string
    endTime?: string
    page?: number
    size?: number
  }) {
    return request.get<PageResult<FundFlow>>('/fund/flow', {
      params: { ...params }
    })
  },

  addIncome(clubId: number, amount: number, description: string) {
    return request.post<void>('/fund/income', null, {
      params: { clubId, amount, description }
    })
  }
}
