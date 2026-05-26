export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

export interface PageResponse<T> {
  code: number
  message: string
  data: T[]
  totalElements: number
  totalPages: number
  currentPage: number
}

export interface User {
  id: number
  username: string
  name: string
  email: string
  age: number
  avatar: string | null
  role: 'ADMIN' | 'USER'
  status: 'ACTIVE' | 'DISABLED'
  createdAt: string
  updatedAt: string
}

export interface LoginRequest {
  username: string
  password: string
}

export interface RegisterRequest {
  username: string
  password: string
  name: string
  email: string
  age: number
}

export interface AuthResponse {
  token: string
  type: string
  id: number
  username: string
  email: string
  name: string
  avatar: string | null
  role: 'ADMIN' | 'USER'
  status: 'ACTIVE' | 'DISABLED'
}

export interface Category {
  id: number
  name: string
  icon: string | null
  color: string | null
  sortOrder: number
  userId: number | null
  createdAt: string
}

export interface CategoryDTO {
  id?: number
  name: string
  icon?: string | null
  color?: string | null
  sortOrder?: number
  userId?: number | null
  createdAt?: string
}

export interface Habit {
  id: number
  name: string
  description: string | null
  icon: string | null
  color: string | null
  repeatType: 'DAILY' | 'WEEKLY'
  repeatDays: string | null
  reminderTime: string | null
  reminderEnabled: boolean
  status: 'ACTIVE' | 'PAUSED' | 'DELETED'
  categoryId: number | null
  categoryName: string | null
  createdAt: string
  updatedAt: string
}

export interface CreateHabitRequest {
  name: string
  description?: string | null
  icon?: string | null
  color?: string | null
  repeatType: 'DAILY' | 'WEEKLY'
  repeatDays?: string | null
  reminderTime?: string | null
  reminderEnabled?: boolean | null
  categoryId?: number | null
}

export interface UpdateHabitRequest {
  name?: string
  description?: string | null
  icon?: string | null
  color?: string | null
  repeatType?: 'DAILY' | 'WEEKLY'
  repeatDays?: string | null
  reminderTime?: string | null
  reminderEnabled?: boolean | null
  categoryId?: number | null
}

export interface CheckRecord {
  id: number
  habitId: number
  habitName: string
  userId: number
  checkDate: string
  checkTime: string
  note: string | null
  createdAt: string
}

export interface CheckRequest {
  habitId: number
  note?: string | null
}

export interface Statistics {
  id: number
  habitId: number
  habitName: string
  totalDays: number
  streakDays: number
  maxStreak: number
  completeRate: number
  lastCheckDate: string | null
}

export interface TodayOverview {
  totalHabits: number
  completedHabits: number
  pendingHabits: number
  completeRate: number
  habits: TodayHabit[]
}

export interface TodayHabit {
  id: number
  name: string
  icon: string | null
  color: string | null
  checked: boolean
  streakDays: number
}

export interface CalendarData {
  month: string
  days: CalendarDay[]
}

export interface CalendarDay {
  day: number
  date: string
  totalHabits: number
  completedHabits: number
  completeRate: number
  habits: HabitCheck[]
}

export interface HabitCheck {
  habitId: number
  habitName: string
  checked: boolean
}

export interface TrendData {
  startDate: string
  endDate: string
  dailyData: DailyData[]
}

export interface DailyData {
  date: string
  totalHabits: number
  completedHabits: number
  completeRate: number
}

export interface HeatmapData {
  year: number
  days: HeatmapDay[]
}

export interface HeatmapDay {
  date: string
  count: number
  level: number
}

export interface CheckinRankingItem {
  rank: number
  userId: number
  userName: string
  userAvatar: string | null
  totalCheckDays: number
  maxStreak: number
  avgCompleteRate: number
  score: number
  currentUser: boolean
}

export interface AchievementRankingItem {
  rank: number
  userId: number
  userName: string
  userAvatar: string | null
  earnedCount: number
  totalAchievements: number
  completionRate: number
  latestEarnedAt: string | null
  currentUser: boolean
}

export interface UploadResponse {
  url: string
}

export interface DashboardStats {
  totalUsers: number
  activeUsers: number
  totalHabits: number
  totalChecks: number
}

export interface HabitTemplate {
  id: number
  name: string
  description: string | null
  icon: string | null
  color: string | null
  repeatType: 'DAILY' | 'WEEKLY'
  repeatDays: string | null
  reminderTime: string | null
  categoryName: string | null
  categoryColor: string | null
  tags: string | null
}

export interface CreateHabitFromTemplateRequest {
  name?: string | null
  reminderTime?: string | null
  reminderEnabled?: boolean | null
  categoryId?: number | null
}

export interface AchievementProgress {
  id: number
  code: string
  name: string
  description: string | null
  icon: string | null
  conditionType: 'TOTAL_CHECKS' | 'MAX_STREAK' | 'TOTAL_HABITS'
  threshold: number
  currentValue: number
  progressPercent: number
  earned: boolean
  earnedAt: string | null
}

export interface ReminderItem {
  habitId: number
  habitName: string
  icon: string | null
  color: string | null
  reminderTime: string
  checked: boolean
}

export interface ReminderSummary {
  total: number
  completed: number
  pending: number
}

export interface AchievementSummary {
  total: number
  earned: number
  inProgress: number
  completionRate: number
}

export interface CommunityComment {
  id: number
  postId: number
  userId: number
  userName: string
  userAvatar: string | null
  content: string
  createdAt: string
}

export interface CommunityPost {
  id: number
  userId: number
  userName: string
  userAvatar: string | null
  content: string
  imagePath: string | null
  commentCount: number
  createdAt: string
  updatedAt: string
  comments: CommunityComment[]
}

export interface CreateCommunityPostRequest {
  content: string
  imagePath?: string | null
}

export interface CreateCommunityCommentRequest {
  content: string
}

export interface AlumniProfile {
  userId: number
  userName: string
  userAvatar: string | null
  school: string | null
  major: string | null
  graduationYear: number | null
  city: string | null
  latitude: number | null
  longitude: number | null
  bio: string | null
  openNearby: boolean
  distanceKm: number | null
}

export interface UpdateAlumniProfileRequest {
  school?: string | null
  major?: string | null
  graduationYear?: number | null
  city?: string | null
  latitude?: number | null
  longitude?: number | null
  bio?: string | null
  openNearby?: boolean
}

export interface AlumniTeam {
  id: number
  name: string
  slogan: string | null
  city: string | null
  latitude: number | null
  longitude: number | null
  maxMembers: number
  currentMembers: number
  status: 'ACTIVE' | 'DISSOLVED'
  ownerId: number
  ownerName: string
  distanceKm: number | null
  joined: boolean
  createdAt: string
}

export interface AlumniTeamMember {
  userId: number
  userName: string
  userAvatar: string | null
  role: 'OWNER' | 'MEMBER'
  totalChecks: number
  avgRate: number
}

export interface AlumniTeamMessage {
  id: number
  teamId: number
  userId: number
  userName: string
  userAvatar: string | null
  content: string
  createdAt: string
}

export interface AlumniTeamDetail {
  team: AlumniTeam
  members: AlumniTeamMember[]
  messages: AlumniTeamMessage[]
}

export interface CreateAlumniTeamRequest {
  name: string
  slogan?: string | null
  city?: string | null
  maxMembers?: number | null
}

export interface AlumniRankingItem {
  rank: number
  userId: number
  userName: string
  userAvatar: string | null
  school: string | null
  city: string | null
  distanceKm: number | null
  totalChecks: number
  avgRate: number
  score: number
}
