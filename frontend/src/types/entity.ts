export interface User {
  id: number
  username: string
  realName: string
  studentId?: string
  phone?: string
  email?: string
  avatar?: string
  role?: Role
  status: number
  position?: string
  joinTime?: string
  createTime: string
  permissions?: string[]
}

export interface Role {
  id: number
  roleName: string
  roleCode: string
  description?: string
}

export interface Club {
  id: number
  clubName: string
  clubCode: string
  description?: string
  logo?: string
  category?: string
  president?: User
  teacher?: User
  balance: number
  memberCount?: number
  status: number
  createTime: string
  monthIncome?: number
  monthExpense?: number
  pendingApprovalCount?: number
}

export interface ClubMember {
  id: number
  clubId: number
  userId: number
  position?: string
  joinTime: string
  status: number
  user?: User
}

export interface FundApply {
  id: number
  applyNo: string
  club?: Club
  applicant?: User
  applyType: string
  amount: number
  reason: string
  vouchers?: string[]
  activity?: Activity
  status: string
  currentStep: number
  approvalRecords?: ApprovalRecord[]
  createTime: string
  updateTime?: string
}

export interface ApprovalRecord {
  id: number
  step: number
  approverName: string
  action: string
  comment?: string
  createTime: string
}

export interface FundFlow {
  id: number
  flowNo: string
  flowType: string
  amount: number
  balanceBefore: number
  balanceAfter: number
  description?: string
  operatorName?: string
  applyNo?: string
  createTime: string
}

export interface Activity {
  id: number
  activityName: string
  description?: string
  startTime: string
  endTime: string
  location?: string
  budget?: number
  status: string
  coverImage?: string
  signupCount?: number
  createTime: string
  club?: Club
}

export interface ActivitySignup {
  id: number
  activityId: number
  userId: number
  signupTime: string
  status: number
}

export interface Notification {
  id: number
  title: string
  content: string
  type: string
  sender?: User
  receiver?: User
  relatedId?: number
  relatedType?: string
  isRead: number
  createTime: string
}

export interface SysLog {
  id: number
  userId?: number
  username?: string
  operation?: string
  method?: string
  params?: string
  ip?: string
  status?: number
  errorMsg?: string
  duration?: number
  createTime: string
}

export interface Statistics {
  clubId: number
  clubName: string
  totalIncome: number
  totalExpense: number
  balance: number
  memberCount: number
  activityCount: number
  pendingApprovalCount: number
}
