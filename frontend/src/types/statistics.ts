export interface CourseStatistics {
  totalCourses: number
  publishedCourses: number
  draftCourses: number
  closedCourses: number
  totalEnrollments: number
  enrollmentsByCourse: Record<string, number>
}

export interface FinanceStatistics {
  totalIncome: number
  totalPending: number
  totalRefunded: number
  totalPayments: number
  successfulPayments: number
  pendingPayments: number
  failedPayments: number
  incomeByMonth: Record<string, number>
}

export interface StudentStatistics {
  totalStudents: number
  activeStudents: number
  inactiveStudents: number
  enrollmentsByStatus: Record<string, number>
  studentsByGender: Record<string, number>
}

export interface StatisticsDTO {
  courseStatistics: CourseStatistics
  financeStatistics: FinanceStatistics
  studentStatistics: StudentStatistics
}

export interface DashboardStats {
  totalUsers: number
  activeUsers: number
  inactiveUsers: number
  adminCount: number
  teacherCount: number
  studentCount: number
}
