import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/layouts/DefaultLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '仪表盘', icon: 'DashboardOutlined' }
      },
      {
        path: 'member',
        name: 'Member',
        meta: { title: '个人中心', roles: ['member', 'president', 'teacher', 'admin'] },
        children: [
          {
            path: 'profile',
            name: 'Profile',
            component: () => import('@/views/member/Profile.vue'),
            meta: { title: '个人信息' }
          },
          {
            path: 'fund',
            name: 'MemberFund',
            component: () => import('@/views/member/FundView.vue'),
            meta: { title: '财务查看' }
          },
          {
            path: 'apply-track',
            name: 'MyApplyTrack',
            component: () => import('@/views/member/MyApplyTrack.vue'),
            meta: { title: '我的申请', roles: ['president', 'admin'] }
          },
          {
            path: 'activity',
            name: 'MemberActivity',
            component: () => import('@/views/member/ActivityList.vue'),
            meta: { title: '活动列表' }
          },
          {
            path: 'activity/calendar',
            name: 'ActivityCalendar',
            component: () => import('@/views/member/ActivityCalendar.vue'),
            meta: { title: '活动日历' }
          },
          {
            path: 'activity/my-signups',
            name: 'MyActivitySignups',
            component: () => import('@/views/member/MyActivitySignups.vue'),
            meta: { title: '我的报名' }
          },
          {
            path: 'notification',
            name: 'Notification',
            component: () => import('@/views/member/Notification.vue'),
            meta: { title: '通知中心' }
          }
        ]
      },
      {
        path: 'president',
        name: 'President',
        meta: { title: '社长管理', roles: ['president', 'admin'] },
        children: [
          {
            path: 'club',
            name: 'ClubManage',
            component: () => import('@/views/president/ClubManage.vue'),
            meta: { title: '社团管理' }
          },
          {
            path: 'member',
            name: 'MemberManage',
            component: () => import('@/views/president/MemberManage.vue'),
            meta: { title: '成员管理' }
          },
          {
            path: 'fund/apply',
            name: 'FundApply',
            component: () => import('@/views/president/FundApply.vue'),
            meta: { title: '资金申请', roles: ['president', 'admin'] }
          },
          {
            path: 'fund/review',
            name: 'FundReview',
            component: () => import('@/views/president/FundReview.vue'),
            meta: { title: '资金审核', roles: ['president', 'admin'] }
          },
          {
            path: 'finance',
            name: 'FinanceReport',
            component: () => import('@/views/president/FinanceReport.vue'),
            meta: { title: '财务报表' }
          },
          {
            path: 'income',
            name: 'IncomeEntry',
            component: () => import('@/views/president/IncomeEntry.vue'),
            meta: { title: '资金入账' }
          },
          {
            path: 'activity/publish',
            name: 'ActivityPublish',
            component: () => import('@/views/president/ActivityPublish.vue'),
            meta: { title: '活动发布' }
          },
          {
            path: 'activity/stats',
            name: 'ActivityStats',
            component: () => import('@/views/president/ActivityStats.vue'),
            meta: { title: '活动统计' }
          }
        ]
      },
      {
        path: 'teacher',
        name: 'Teacher',
        meta: { title: '指导老师', roles: ['teacher', 'admin'] },
        children: [
          {
            path: 'clubs',
            name: 'TeacherClubs',
            component: () => import('@/views/teacher/ClubMonitor.vue'),
            meta: { title: '社团监管' }
          },
          {
            path: 'approval',
            name: 'TeacherApproval',
            component: () => import('@/views/teacher/ApprovalList.vue'),
            meta: { title: '审批列表', roles: ['teacher', 'admin'] }
          },
          {
            path: 'approval/:id',
            name: 'ApprovalDetail',
            component: () => import('@/views/teacher/ApprovalDetail.vue'),
            meta: { title: '审批详情' }
          },
          {
            path: 'warning',
            name: 'RiskWarning',
            component: () => import('@/views/teacher/RiskWarning.vue'),
            meta: { title: '风险预警' }
          },
          {
            path: 'activity/review',
            name: 'ActivityReview',
            component: () => import('@/views/teacher/ActivityReview.vue'),
            meta: { title: '活动审核' }
          },
          {
            path: 'activity/ledger',
            name: 'ActivityLedger',
            component: () => import('@/views/teacher/ActivityLedger.vue'),
            meta: { title: '活动台账' }
          }
        ]
      },
      {
        path: 'admin',
        name: 'Admin',
        meta: { title: '系统管理', roles: ['admin'] },
        children: [
          {
            path: 'system',
            name: 'SystemConfig',
            component: () => import('@/views/admin/SystemConfig.vue'),
            meta: { title: '系统配置' }
          },
          {
            path: 'user',
            name: 'UserManage',
            component: () => import('@/views/admin/UserManage.vue'),
            meta: { title: '用户管理' }
          },
          {
            path: 'club',
            name: 'AdminClub',
            component: () => import('@/views/admin/ClubManage.vue'),
            meta: { title: '社团管理' }
          },
          {
            path: 'monitor',
            name: 'DataMonitor',
            component: () => import('@/views/admin/DataMonitor.vue'),
            meta: { title: '数据监控' }
          },
          {
            path: 'log',
            name: 'LogManage',
            component: () => import('@/views/admin/LogManage.vue'),
            meta: { title: '日志管理' }
          },
          {
            path: 'operation',
            name: 'OperationCenter',
            component: () => import('@/views/admin/OperationCenter.vue'),
            meta: { title: '运营总览' }
          }
        ]
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()
  const token = localStorage.getItem('token')

  if (to.meta.requiresAuth !== false && !token) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
    return
  }

  if (token && !userStore.user) {
    await userStore.fetchUserInfo()
  }

  if (to.meta.roles && to.meta.roles.length > 0) {
    const userRole = userStore.user?.role?.roleCode
    if (!userRole || !to.meta.roles.includes(userRole)) {
      next({ name: 'Dashboard' })
      return
    }
  }

  document.title = `${to.meta.title || '首页'} - 高校社团资金管控平台`
  next()
})

export default router
