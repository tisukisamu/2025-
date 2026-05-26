import request from '../utils/request'
import type { 
  ApiResponse, 
  Statistics,
  TrendData,
  HeatmapData,
  CheckinRankingItem,
  AchievementRankingItem,
  AchievementProgress,
  AchievementSummary
} from '../types'

export function getHabitStatistics(habitId: number): Promise<ApiResponse<Statistics>> {
  return request({
    url: `/statistics/habit/${habitId}`,
    method: 'get'
  })
}

export function getAllStatistics(): Promise<ApiResponse<Statistics[]>> {
  return request({
    url: '/statistics/all',
    method: 'get'
  })
}

export function getTrend(start: string, end: string): Promise<ApiResponse<TrendData>> {
  return request({
    url: '/statistics/trend',
    method: 'get',
    params: { start, end }
  })
}

export function getHeatmap(year: number): Promise<ApiResponse<HeatmapData>> {
  return request({
    url: '/statistics/heatmap',
    method: 'get',
    params: { year }
  })
}

export function getCheckinRanking(limit: number = 10): Promise<ApiResponse<CheckinRankingItem[]>> {
  return request({
    url: '/statistics/ranking',
    method: 'get',
    params: { limit }
  })
}

export function getMyAchievements(params?: {
  earnedOnly?: boolean
  conditionType?: 'TOTAL_CHECKS' | 'MAX_STREAK' | 'TOTAL_HABITS'
  sortBy?: 'DEFAULT' | 'PROGRESS_DESC' | 'LATEST_EARNED'
}): Promise<ApiResponse<AchievementProgress[]>> {
  return request({
    url: '/achievements/me',
    method: 'get',
    params
  })
}

export function evaluateAchievements(): Promise<ApiResponse<boolean>> {
  return request({
    url: '/achievements/evaluate',
    method: 'post'
  })
}

export function getAchievementSummary(): Promise<ApiResponse<AchievementSummary>> {
  return request({
    url: '/achievements/me/summary',
    method: 'get'
  })
}

export function getAchievementRanking(limit: number = 20): Promise<ApiResponse<AchievementRankingItem[]>> {
  return request({
    url: '/achievements/ranking',
    method: 'get',
    params: { limit }
  })
}
