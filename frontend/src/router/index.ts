import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/product/list',
    name: 'ProductList',
    component: () => import('../views/ProductList.vue')
  },
  {
    path: '/product/new',
    name: 'NewProducts',
    component: () => import('../views/NewProducts.vue')
  },
  {
    path: '/product/hot',
    name: 'HotProducts',
    component: () => import('../views/HotProducts.vue')
  },
  {
    path: '/product/:id',
    name: 'ProductDetail',
    component: () => import('../views/ProductDetail.vue')
  },
  {
    path: '/cart',
    name: 'Cart',
    component: () => import('../views/Cart.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/orders',
    name: 'Orders',
    component: () => import('../views/Orders.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/favorites',
    name: 'MyFavorites',
    component: () => import('../views/MyFavorites.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/order/tracking',
    name: 'OrderTracking',
    component: () => import('../views/OrderTracking.vue')
  },
  {
    path: '/orders/:id',
    name: 'OrderDetail',
    component: () => import('../views/OrderTracking.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/search',
    name: 'ProductSearch',
    component: () => import('../views/ProductSearch.vue')
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('../views/admin/Layout.vue'),
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('../views/admin/Dashboard.vue')
      },
      {
        path: 'products',
        name: 'AdminProducts',
        component: () => import('../views/admin/Products.vue')
      },
      {
        path: 'orders',
        name: 'AdminOrders',
        component: () => import('../views/admin/Orders.vue')
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('../views/admin/Users.vue')
      },
      {
        path: 'store-audit',
        name: 'AdminStoreAudit',
        component: () => import('../views/admin/StoreAudit.vue')
      },
      {
        path: 'product-audit',
        name: 'AdminProductAudit',
        component: () => import('../views/admin/ProductAudit.vue')
      },
      {
        path: 'profile',
        name: 'AdminProfile',
        component: () => import('../views/admin/Profile.vue')
      },
      {
        path: 'settings',
        name: 'AdminSettings',
        component: () => import('../views/admin/Settings.vue')
      }
    ]
  },
  {
    path: '/store/register',
    name: 'StoreRegister',
    component: () => import('../views/StoreRegister.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/store',
    name: 'Store',
    component: () => import('../views/store/StoreLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'StoreDashboard',
        component: () => import('../views/store/StoreDashboard.vue')
      },
      {
        path: 'products',
        name: 'StoreProducts',
        component: () => import('../views/store/StoreProducts.vue')
      },
      {
        path: 'orders',
        name: 'StoreOrders',
        component: () => import('../views/store/StoreOrders.vue')
      },
      {
        path: 'stats',
        name: 'StoreStats',
        component: () => import('../views/store/StoreStats.vue')
      },
      {
        path: 'settings',
        name: 'StoreSettings',
        component: () => import('../views/store/StoreSettings.vue')
      },
      {
        path: 'profile',
        name: 'StoreProfile',
        component: () => import('../views/admin/Profile.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  if (to.meta.requiresAuth && !userStore.token) {
    next('/login')
  } else if (to.meta.requiresAdmin && userStore.role !== 'ROLE_ADMIN') {
    next('/')
  } else {
    next()
  }
})

export default router
