import request from '../utils/request'
import type {
  AlumniProfile,
  AlumniRankingItem,
  AlumniTeam,
  AlumniTeamDetail,
  AlumniTeamMessage,
  ApiResponse,
  CreateAlumniTeamRequest,
  UpdateAlumniProfileRequest
} from '../types'

export function getMyAlumniProfile(): Promise<ApiResponse<AlumniProfile>> {
  return request({
    url: '/alumni/profile/me',
    method: 'get'
  })
}

export function updateMyAlumniProfile(data: UpdateAlumniProfileRequest): Promise<ApiResponse<AlumniProfile>> {
  return request({
    url: '/alumni/profile/me',
    method: 'put',
    data
  })
}

export function getNearbyAlumni(radiusKm = 10): Promise<ApiResponse<AlumniProfile[]>> {
  return request({
    url: '/alumni/nearby',
    method: 'get',
    params: { radiusKm }
  })
}

export function getAlumniRanking(radiusKm = 10, limit = 20): Promise<ApiResponse<AlumniRankingItem[]>> {
  return request({
    url: '/alumni/ranking',
    method: 'get',
    params: { radiusKm, limit }
  })
}

export function createAlumniTeam(data: CreateAlumniTeamRequest): Promise<ApiResponse<AlumniTeam>> {
  return request({
    url: '/alumni/teams',
    method: 'post',
    data
  })
}

export function getNearbyAlumniTeams(radiusKm = 10): Promise<ApiResponse<AlumniTeam[]>> {
  return request({
    url: '/alumni/teams/nearby',
    method: 'get',
    params: { radiusKm }
  })
}

export function getMyAlumniTeams(): Promise<ApiResponse<AlumniTeam[]>> {
  return request({
    url: '/alumni/teams/my',
    method: 'get'
  })
}

export function getAlumniTeamDetail(teamId: number): Promise<ApiResponse<AlumniTeamDetail>> {
  return request({
    url: `/alumni/teams/${teamId}`,
    method: 'get'
  })
}

export function joinAlumniTeam(teamId: number): Promise<ApiResponse<AlumniTeam>> {
  return request({
    url: `/alumni/teams/${teamId}/join`,
    method: 'post'
  })
}

export function quitAlumniTeam(teamId: number): Promise<ApiResponse<void>> {
  return request({
    url: `/alumni/teams/${teamId}/quit`,
    method: 'post'
  })
}

export function getAlumniTeamMessages(teamId: number): Promise<ApiResponse<AlumniTeamMessage[]>> {
  return request({
    url: `/alumni/teams/${teamId}/messages`,
    method: 'get'
  })
}

export function createAlumniTeamMessage(teamId: number, content: string): Promise<ApiResponse<AlumniTeamMessage>> {
  return request({
    url: `/alumni/teams/${teamId}/messages`,
    method: 'post',
    data: { content }
  })
}
