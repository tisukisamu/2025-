import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'
import { companyApi } from '../api'
import MainLayout from '../components/MainLayout.vue'

const routes = [
  // 公开路由
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/auth/Login.vue'),
    meta: { public: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/auth/Register.vue'),
    meta: { public: true }
  },
  // 需要登录的路由
  {
    path: '/',
    component: MainLayout,
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('../views/Home.vue')
      },
      {
        path: 'about',
        name: 'About',
        component: () => import('../views/About.vue')
      },
      {
        path: 'users',
        name: 'Users',
        component: () => import('../views/Users.vue'),
        meta: { requireAuth: true }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('../views/Profile.vue'),
        meta: { requireAuth: true }
      },
      {
        path: 'jobs',
        name: 'Jobs',
        component: () => import('../views/jobs/JobList.vue')
      },
      {
        path: 'jobs/:id',
        name: 'JobDetail',
        component: () => import('../views/jobs/JobDetail.vue')
      },
      {
        path: 'resumes',
        name: 'Resumes',
        component: () => import('../views/resumes/ResumeList.vue'),
        meta: { requireAuth: true }
      },
      {
        path: 'applications',
        name: 'Applications',
        component: () => import('../views/applications/ApplicationList.vue'),
        meta: { requireAuth: true }
      },
      {
        path: 'applications/:id',
        name: 'ApplicationDetail',
        component: () => import('../views/applications/ApplicationDetail.vue'),
        meta: { requireAuth: true }
      },
      {
        path: 'my-applications',
        name: 'MyApplications',
        component: () => import('../views/applications/MyApplications.vue'),
        meta: { requireAuth: true }
      },
      {
        path: 'favorites',
        name: 'Favorites',
        component: () => import('../views/favorites/FavoriteList.vue'),
        meta: { requireAuth: true }
      },
      {
        path: 'company/apply',
        name: 'CompanyApply',
        component: () => import('../views/company/CompanyProfile.vue'),
        meta: { requireAuth: true }
      },
      {
        path: 'company',
        name: 'Company',
        component: () => import('../views/company/JobManage.vue'),
        meta: { requireAuth: true, requireCompany: true }
      },
      {
        path: 'company/profile',
        name: 'CompanyProfile',
        component: () => import('../views/company/CompanyProfile.vue'),
        meta: { requireAuth: true }
      },
      {
        path: 'company/resumes',
        name: 'CompanyResumes',
        component: () => import('../views/company/ResumeReceived.vue'),
        meta: { requireAuth: true, requireCompany: true }
      },
      {
        path: 'company/interviews',
        name: 'CompanyInterviews',
        component: () => import('../views/company/InterviewManage.vue'),
        meta: { requireAuth: true, requireCompany: true }
      },
      {
        path: 'company/talents',
        name: 'CompanyTalents',
        component: () => import('../views/company/TalentPool.vue'),
        meta: { requireAuth: true, requireCompany: true }
      },
      {
        path: 'messages',
        name: 'Messages',
        component: () => import('../views/messages/MessageList.vue'),
        meta: { requireAuth: true }
      },
      // 管理员路由
      {
        path: 'admin',
        name: 'Admin',
        component: () => import('../views/admin/Dashboard.vue'),
        meta: { requireAuth: true, requireAdmin: true }
      },
      {
        path: 'admin/users',
        name: 'AdminUsers',
        component: () => import('../views/admin/UserManage.vue'),
        meta: { requireAuth: true, requireAdmin: true }
      },
      {
        path: 'admin/companies',
        name: 'AdminCompanies',
        component: () => import('../views/admin/CompanyAudit.vue'),
        meta: { requireAuth: true, requireAdmin: true }
      },
      {
        path: 'admin/companies/:id',
        name: 'AdminCompanyDetail',
        component: () => import('../views/admin/CompanyDetail.vue'),
        meta: { requireAuth: true, requireAdmin: true }
      },
      {
        path: 'admin/jobs',
        name: 'AdminJobs',
        component: () => import('../views/admin/JobAudit.vue'),
        meta: { requireAuth: true, requireAdmin: true }
      },
      {
        path: 'admin/announcements',
        name: 'AdminAnnouncements',
        component: () => import('../views/admin/AnnouncementManage.vue'),
        meta: { requireAuth: true, requireAdmin: true }
      },
      {
        path: 'admin/statistics',
        name: 'AdminStatistics',
        component: () => import('../views/admin/Statistics.vue'),
        meta: { requireAuth: true, requireAdmin: true }
      },
      {
        path: 'admin/complaints',
        name: 'AdminComplaints',
        component: () => import('../views/admin/ComplaintManage.vue'),
        meta: { requireAuth: true, requireAdmin: true }
      }
    ]
  },
  // 404
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('../views/NotFound.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach(async (to, _from, next) => {
  const userStore = useUserStore()
  
  // 初始化用户信息
  if (!userStore.userInfo && userStore.token) {
    await userStore.init()
  }
  
  // 公开路由直接放行
  if (to.meta.public) {
    // 已登录用户访问登录/注册页，跳转到首页
    if (userStore.isLoggedIn) {
      next('/')
    } else {
      next()
    }
    return
  }
  
  // 需要登录
  if (to.meta.requireAuth) {
    if (!userStore.isLoggedIn) {
      next('/login')
      return
    }
    
    // 检查账户状态
    if (!userStore.isActive) {
      // 账户被禁用
      return next('/login')
    }
    
    // 需要管理员权限
    if (to.meta.requireAdmin && !userStore.isAdmin) {
      // 无权限
      return next('/')
    }

    if (to.meta.requireCompany && userStore.userRole !== 'COMPANY') {
      try {
        const res = await companyApi.getMy()
        if (res.data?.status === 'APPROVED') {
          return next()
        }
        return next('/company/apply')
      } catch (error) {
        return next('/company/apply')
      }
    }
  }
  
  next()
})

export default router
