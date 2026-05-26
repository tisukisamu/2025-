import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import MainLayout from '@/components/MainLayout.vue'
import AdminLayout from '@/components/AdminLayout.vue'

const routes = [
  {
    path: '/',
    component: MainLayout,
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/Home.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'products',
        name: 'ProductList',
        component: () => import('@/views/product/ProductList.vue'),
        meta: { title: '商品列表' }
      },
      {
        path: 'products/:id',
        name: 'ProductDetail',
        component: () => import('@/views/product/ProductDetail.vue'),
        meta: { title: '商品详情' }
      },
      {
        path: 'publish',
        name: 'ProductPublish',
        component: () => import('@/views/product/ProductPublish.vue'),
        meta: { title: '发布商品', requiresAuth: true }
      },
      {
        path: 'products/mine',
        name: 'MyProducts',
        component: () => import('@/views/product/MyProducts.vue'),
        meta: { title: '我的商品', requiresAuth: true }
      },
      {
        path: 'orders',
        name: 'OrderList',
        component: () => import('@/views/order/OrderList.vue'),
        meta: { title: '我的订单', requiresAuth: true }
      },
      {
        path: 'orders/create',
        name: 'CreateOrder',
        component: () => import('@/views/order/CreateOrder.vue'),
        meta: { title: '创建订单', requiresAuth: true }
      },
      {
        path: 'orders/:id',
        name: 'OrderDetail',
        component: () => import('@/views/order/OrderDetail.vue'),
        meta: { title: '订单详情', requiresAuth: true }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/user/Profile.vue'),
        meta: { title: '个人中心', requiresAuth: true }
      },
      {
        path: 'favorites',
        name: 'Favorites',
        component: () => import('@/views/user/Favorites.vue'),
        meta: { title: '我的收藏', requiresAuth: true }
      },
      {
        path: 'following',
        name: 'Following',
        component: () => import('@/views/user/Following.vue'),
        meta: { title: '我的关注', requiresAuth: true }
      },
      {
        path: 'user/:id',
        name: 'UserProfile',
        component: () => import('@/views/user/UserProfile.vue'),
        meta: { title: '用户主页' }
      },
      {
        path: 'messages',
        name: 'Messages',
        component: () => import('@/views/message/MessageList.vue'),
        meta: { title: '消息中心', requiresAuth: true }
      },
      {
        path: 'chat/:userId',
        name: 'Chat',
        component: () => import('@/views/message/Chat.vue'),
        meta: { title: '聊天', requiresAuth: true }
      },
      {
        path: 'announcements',
        name: 'Announcements',
        component: () => import('@/views/announcement/AnnouncementList.vue'),
        meta: { title: '系统公告' }
      },
      {
        path: 'announcements/:id',
        name: 'AnnouncementDetail',
        component: () => import('@/views/announcement/AnnouncementDetail.vue'),
        meta: { title: '公告详情' }
      },
      {
        path: 'feedback',
        name: 'Feedback',
        component: () => import('@/views/feedback/FeedbackList.vue'),
        meta: { title: '意见反馈', requiresAuth: true }
      },
      {
        path: 'feedback/create',
        name: 'FeedbackCreate',
        component: () => import('@/views/feedback/FeedbackCreate.vue'),
        meta: { title: '提交反馈', requiresAuth: true }
      },
      {
        path: 'buy-requests',
        name: 'BuyRequestList',
        component: () => import('@/views/buyrequest/BuyRequestList.vue'),
        meta: { title: '求购专区' }
      },
      {
        path: 'buy-requests/:id',
        name: 'BuyRequestDetail',
        component: () => import('@/views/buyrequest/BuyRequestDetail.vue'),
        meta: { title: '求购详情' }
      },
      {
        path: 'topics',
        name: 'TopicList',
        component: () => import('@/views/topic/TopicList.vue'),
        meta: { title: '话题讨论' }
      },
      {
        path: 'topics/:id',
        name: 'TopicDetail',
        component: () => import('@/views/topic/TopicDetail.vue'),
        meta: { title: '话题详情' }
      },
      {
        path: 'credit',
        name: 'CreditCenter',
        component: () => import('@/views/credit/CreditCenter.vue'),
        meta: { title: '信用中心', requiresAuth: true }
      }
    ]
  },
  {
    path: '/admin',
    component: AdminLayout,
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      {
        path: '',
        name: 'Admin',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '管理后台', requiresAuth: true, requiresAdmin: true }
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/UserManage.vue'),
        meta: { title: '用户管理', requiresAuth: true, requiresAdmin: true }
      },
      {
        path: 'products',
        name: 'AdminProducts',
        component: () => import('@/views/admin/ProductManage.vue'),
        meta: { title: '商品管理', requiresAuth: true, requiresAdmin: true }
      },
      {
        path: 'orders',
        name: 'AdminOrders',
        component: () => import('@/views/admin/OrderManage.vue'),
        meta: { title: '订单管理', requiresAuth: true, requiresAdmin: true }
      },
      {
        path: 'reports',
        name: 'AdminReports',
        component: () => import('@/views/admin/ReportManage.vue'),
        meta: { title: '举报管理', requiresAuth: true, requiresAdmin: true }
      },
      {
        path: 'announcements',
        name: 'AdminAnnouncements',
        component: () => import('@/views/admin/AnnouncementManage.vue'),
        meta: { title: '公告管理', requiresAuth: true, requiresAdmin: true }
      },
      {
        path: 'feedbacks',
        name: 'AdminFeedbacks',
        component: () => import('@/views/admin/FeedbackManage.vue'),
        meta: { title: '反馈管理', requiresAuth: true, requiresAdmin: true }
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue'),
    meta: { title: '登录', guest: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/Register.vue'),
    meta: { title: '注册', guest: true }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/NotFound.vue'),
    meta: { title: '页面不存在' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

router.beforeEach(async (to, _from, next) => {
  const userStore = useUserStore()

  document.title = to.meta.title ? `${to.meta.title} - 校园置换` : '校园闲置电子产品置换系统'

  if (userStore.token && !userStore.userInfo) {
    await userStore.init()
  }

  const isLoggedIn = userStore.isLoggedIn
  const isAdmin = userStore.isAdmin

  if (to.meta.requiresAuth && !isLoggedIn) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
  } else if (to.meta.requiresAdmin && !isAdmin) {
    next({ name: 'Home' })
  } else if (to.meta.guest && isLoggedIn) {
    next({ name: 'Home' })
  } else {
    next()
  }
})

export default router
