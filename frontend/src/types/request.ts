export interface LoginRequest {
  username: string
  password: string
}

export interface UserCreateRequest {
  username: string
  password: string
  realName: string
  studentId?: string
  phone?: string
  email?: string
  roleId?: number
}

export interface UserUpdateRequest {
  realName?: string
  phone?: string
  email?: string
  avatar?: string
}

export interface PasswordChangeRequest {
  oldPassword: string
  newPassword: string
}

export interface ClubCreateRequest {
  clubName: string
  clubCode: string
  description?: string
  logo?: string
  category?: string
  presidentId?: number
  teacherId?: number
}

export interface FundApplyRequest {
  clubId: number
  applyType: string
  amount: number
  reason: string
  vouchers?: string[]
  activityId?: number
}

export interface ApprovalRequest {
  comment?: string
}

export interface ActivityCreateRequest {
  clubId: number
  activityName: string
  description?: string
  startTime: string
  endTime: string
  location?: string
  budget?: number
  coverImage?: string
}
