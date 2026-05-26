import { request } from '@/utils/request'
import type { SysLog } from '@/types'
import type { PageResult } from '@/types/response'

export const logApi = {
  getList(params?: {
    username?: string
    operation?: string
    status?: number
    page?: number
    size?: number
  }) {
    return request.get<PageResult<SysLog>>('/log/list', {
      params: { ...params }
    })
  }
}
