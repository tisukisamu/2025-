import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import MainLayout from '@/layouts/MainLayout.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue'),
    meta: { public: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/Register.vue'),
    meta: { public: true }
  },
  {
    path: '/',
    component: MainLayout,
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/Home.vue')
      },
      {
        path: 'about',
        name: 'About',
        component: () => import('@/views/About.vue')
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue')
      },
      {
        path: 'admin',
        meta: { requiresAdmin: true },
        children: [
          {
            path: '',
            name: 'AdminDashboard',
            component: () => import('@/views/admin/Dashboard.vue')
          },
          {
            path: 'users',
            name: 'UserManage',
            component: () => import('@/views/admin/UserManage.vue')
          },
          {
            path: 'courses',
            name: 'CourseManage',
            component: () => import('@/views/admin/CourseManage.vue')
          },
          {
            path: 'courses/new',
            name: 'CourseCreate',
            component: () => import('@/views/admin/CourseEdit.vue')
          },
          {
            path: 'courses/:id/edit',
            name: 'CourseEdit',
            component: () => import('@/views/admin/CourseEdit.vue')
          },
          {
            path: 'courses/:id',
            name: 'AdminCourseDetail',
            component: () => import('@/views/CourseDetail.vue')
          },
          {
            path: 'teachers',
            name: 'TeacherManage',
            component: () => import('@/views/admin/TeacherManage.vue')
          },
          {
            path: 'teachers/new',
            name: 'TeacherCreate',
            component: () => import('@/views/admin/TeacherEdit.vue')
          },
          {
            path: 'teachers/:id/edit',
            name: 'TeacherEdit',
            component: () => import('@/views/admin/TeacherEdit.vue')
          },
          {
            path: 'teachers/:id',
            name: 'AdminTeacherDetail',
            component: () => import('@/views/TeacherDetail.vue')
          },
          {
            path: 'students',
            name: 'StudentManage',
            component: () => import('@/views/admin/StudentManage.vue')
          },
          {
            path: 'students/:id',
            name: 'AdminStudentDetail',
            component: () => import('@/views/StudentDetail.vue')
          },
          {
            path: 'schedules',
            name: 'ScheduleManage',
            component: () => import('@/views/admin/ScheduleManage.vue')
          },
          {
            path: 'enrollments',
            name: 'EnrollmentManage',
            component: () => import('@/views/admin/EnrollmentManage.vue')
          },
          {
            path: 'payments',
            name: 'PaymentManage',
            component: () => import('@/views/admin/FinanceManage.vue')
          },
          {
            path: 'bills',
            name: 'BillManage',
            component: () => import('@/views/admin/FinanceManage.vue')
          },
          {
            path: 'statistics',
            name: 'Statistics',
            component: () => import('@/views/admin/Statistics.vue')
          },
          {
            path: 'settings',
            name: 'Settings',
            component: () => import('@/views/admin/Settings.vue')
          },
          {
            path: 'notifications',
            name: 'Notifications',
            component: () => import('@/views/admin/Notifications.vue')
          },
          {
            path: 'payment-history',
            name: 'AdminPaymentHistory',
            component: () => import('@/views/PaymentHistory.vue')
          },
          {
            path: 'calendar',
            name: 'AdminScheduleCalendar',
            component: () => import('@/views/ScheduleCalendar.vue')
          }
        ]
      },
      {
        path: 'teacher',
        meta: { requiresTeacher: true },
        children: [
          {
            path: '',
            name: 'TeacherDashboard',
            component: () => import('@/views/teacher/Dashboard.vue')
          },
          {
            path: 'courses',
            name: 'TeacherCourses',
            component: () => import('@/views/teacher/MyCourses.vue')
          },
          {
            path: 'courses/:id/edit',
            name: 'TeacherCourseEdit',
            component: () => import('@/views/admin/CourseEdit.vue')
          },
          {
            path: 'schedules',
            name: 'TeacherSchedules',
            component: () => import('@/views/teacher/MySchedules.vue')
          },
          {
            path: 'students',
            name: 'TeacherStudents',
            component: () => import('@/views/teacher/Students.vue')
          }
        ]
      },
      {
        path: 'student',
        children: [
          {
            path: '',
            name: 'StudentDashboard',
            component: () => import('@/views/student/Dashboard.vue')
          },
          {
            path: 'courses',
            name: 'StudentCourses',
            component: () => import('@/views/student/CourseList.vue')
          },
          {
            path: 'my-courses',
            name: 'StudentMyCourses',
            component: () => import('@/views/student/MyCourses.vue')
          },
          {
            path: 'finance',
            name: 'StudentFinance',
            component: () => import('@/views/student/Finance.vue')
          },
          {
            path: 'profile',
            name: 'StudentProfile',
            component: () => import('@/views/student/Profile.vue')
          }
        ]
      }
    ]
  },
  {
    path: '/courses/:id',
    component: MainLayout,
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'CourseDetail',
        component: () => import('@/views/CourseDetail.vue')
      }
    ]
  },
  {
    path: '/teachers/:id',
    component: MainLayout,
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'TeacherDetail',
        component: () => import('@/views/TeacherDetail.vue')
      }
    ]
  },
  {
    path: '/students/:id',
    component: MainLayout,
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'StudentDetail',
        component: () => import('@/views/StudentDetail.vue')
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()

  if (userStore.token && !userStore.userInfo) {
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

  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next('/login')
  } else if (to.meta.requiresAdmin && !userStore.isAdmin) {
    next('/')
  } else if (to.meta.requiresTeacher && !userStore.isTeacher && !userStore.isAdmin) {
    next('/')
  } else {
    next()
  }
})

export default router
