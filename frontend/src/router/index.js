import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'
import MainLayout from '../components/MainLayout.vue'

const routes = [
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
        path: 'services',
        name: 'Services',
        component: () => import('../views/service/ServiceList.vue')
      },
      {
        path: 'pets',
        name: 'Pets',
        component: () => import('../views/pet/PetList.vue'),
        meta: { requireAuth: true }
      },
      {
        path: 'appointments',
        name: 'Appointments',
        component: () => import('../views/appointment/AppointmentList.vue'),
        meta: { requireAuth: true }
      },
      {
        path: 'appointments/create',
        name: 'CreateAppointment',
        component: () => import('../views/appointment/CreateAppointment.vue'),
        meta: { requireAuth: true }
      },
      {
        path: 'appointments/:id/process',
        name: 'AppointmentProcess',
        component: () => import('../views/appointment/ProcessDetail.vue'),
        meta: { requireAuth: true }
      },
      {
        path: 'memorials',
        name: 'Memorials',
        component: () => import('../views/memorial/MemorialList.vue')
      },
      {
        path: 'memorials/create',
        name: 'CreateMemorial',
        component: () => import('../views/memorial/MemorialList.vue'),
        meta: { requireAuth: true }
      },
      {
        path: 'memorials/:id',
        name: 'MemorialDetail',
        component: () => import('../views/memorial/MemorialDetail.vue')
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('../views/Profile.vue'),
        meta: { requireAuth: true }
      },
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
        path: 'admin/appointments',
        name: 'AdminAppointments',
        component: () => import('../views/admin/AppointmentManage.vue'),
        meta: { requireAuth: true, requireAdmin: true }
      },
      {
        path: 'admin/services',
        name: 'AdminServices',
        component: () => import('../views/admin/ServiceManage.vue'),
        meta: { requireAuth: true, requireAdmin: true }
      },
      {
        path: 'admin/pets',
        name: 'AdminPets',
        component: () => import('../views/admin/PetManage.vue'),
        meta: { requireAuth: true, requireAdmin: true }
      },
      {
        path: 'admin/process',
        name: 'AdminProcess',
        component: () => import('../views/admin/ProcessManage.vue'),
        meta: { requireAuth: true, requireAdmin: true }
      },
      {
        path: 'admin/statistics',
        name: 'AdminStatistics',
        component: () => import('../views/admin/Statistics.vue'),
        meta: { requireAuth: true, requireAdmin: true }
      },
      {
        path: 'service/tasks',
        name: 'ServiceTasks',
        component: () => import('../views/service/TaskList.vue'),
        meta: { requireAuth: true, requireService: true }
      },
      {
        path: 'service/task/:id',
        name: 'ServiceTaskDetail',
        component: () => import('../views/service/TaskDetail.vue'),
        meta: { requireAuth: true, requireService: true }
      },
      {
        path: 'service/process/:id',
        name: 'ServiceProcess',
        component: () => import('../views/service/ProcessUpdate.vue'),
        meta: { requireAuth: true, requireService: true }
      },
      {
        path: 'service/record',
        name: 'ServiceRecord',
        component: () => import('../views/service/Record.vue'),
        meta: { requireAuth: true, requireService: true }
      }
    ]
  },
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

router.beforeEach(async (to, _from, next) => {
  const userStore = useUserStore()
  
  if (!userStore.userInfo && userStore.token) {
    await userStore.init()
  }
  
  if (to.meta.public) {
    if (userStore.isLoggedIn) {
      next('/')
    } else {
      next()
    }
    return
  }
  
  if (to.meta.requireAuth) {
    if (!userStore.isLoggedIn) {
      next('/login')
      return
    }
    
    if (!userStore.isActive) {
      return next('/login')
    }
    
    if (to.meta.requireAdmin && !userStore.isAdmin) {
      return next('/')
    }

    if (to.meta.requireService) {
      const role = userStore.userRole
      if (role !== 'SERVICE' && role !== 'ADMIN') {
        return next('/')
      }
    }
  }
  
  next()
})

export default router
