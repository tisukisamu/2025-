import { request } from '@/utils/request'
import type { ApprovalRequest } from '@/types/request'
import type { FundApply } from '@/types'
import type { PageResult } from '@/types/response'

export const approvalApi = {
  getPendingList(page: number = 0, size: number = 10) {
    return request.get<PageResult<FundApply>>('/approval/list', {
      params: { page, size }
    })
  },

  approve(id: number, data: ApprovalRequest) {
    return request.post<void>(`/approval/${id}/approve`, data)
  },

  reject(id: number, data: ApprovalRequest) {
    return request.post<void>(`/approval/${id}/reject`, data)
  }
}
