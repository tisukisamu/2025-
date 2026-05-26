import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'
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
      // 管理员路由
      {
        path: 'admin',
        name: 'Admin',
        component: () => import('../views/admin/Dashboard.vue'),
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
  }
  
  next()
})

export default router
