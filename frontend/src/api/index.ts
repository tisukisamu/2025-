import axios, { AxiosResponse } from 'axios'
import { useUserStore } from '../stores/user'
import { message } from 'ant-design-vue'

// 创建 axios 实例
const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器 - 添加 Token
api.interceptors.request.use(
  (config) => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器 - 统一错误处理
api.interceptors.response.use(
  (response: AxiosResponse) => response.data,
  (error) => {
    const status = error.response?.status
    const responseData = error.response?.data
    // 处理纯文本错误响应或JSON格式错误响应
    let errorMessage: string
    if (typeof responseData === 'string') {
      errorMessage = responseData
    } else if (responseData?.message) {
      errorMessage = responseData.message
    } else {
      errorMessage = error.message || '请求失败'
    }
    
    // 处理 401 未授权错误
    if (status === 401) {
      // 检查是否是登录接口的错误
      const isLoginRequest = error.config?.url?.includes('/auth/login')
      if (!isLoginRequest) {
        const userStore = useUserStore()
        userStore.logout()
        message.error('登录已过期，请重新登录')
        window.location.href = '/login'
      }
    } else if (status === 403) {
      message.error('没有权限执行此操作')
    } else if (status === 404) {
      message.error('请求的资源不存在')
    } else if (status === 500) {
      message.error('服务器内部错误，请稍后重试')
    } else if (status === 400) {
      // 400 错误通常是业务逻辑错误，显示后端返回的具体错误信息
      message.error(errorMessage)
    } else {
      // 其他错误显示具体错误信息
      message.error(errorMessage)
    }
    
    return Promise.reject(new Error(errorMessage))
  }
)

// ==================== 类型定义 ====================

// 通用响应类型
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

// 分页响应类型
export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}

// 用户相关类型
export interface User {
  id: number
  username: string
  nickname: string
  role: 'ROLE_USER' | 'ROLE_ADMIN'
}

export interface LoginRequest {
  username: string
  password: string
}

export interface RegisterRequest {
  username: string
  password: string
  nickname: string
}

export interface LoginResponse {
  accessToken: string
  tokenType: string
  userId: number
  username: string
  role: string
}

// 商品相关类型
export interface Product {
  id: number
  name: string
  description: string
  price: number
  stock: number
  // 原始图片字段（保留兼容）
  imageUrl: string
  imageUrls?: string
  // 图片双保底策略字段
  primaryImageUrl?: string
  primaryImageType?: 'relative' | 'absolute' | 'fallback' | 'none'
  fallbackImageUrl?: string
  allImageUrls?: string[]
  imagePathType?: string
  imagePathTypeDesc?: string
  active: boolean
  category: string
  isNew?: boolean
  isHot?: boolean
  sales?: number
}

// 分页结果类型
export interface PageResult<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  first: boolean
  last: boolean
  empty: boolean
}

export interface CreateProductRequest {
  name: string
  description: string
  price: number
  stock: number
  imageUrl: string
  category: string
}

export interface UpdateProductRequest extends Partial<CreateProductRequest> {
  active?: boolean
}

// 订单相关类型
export type OrderStatus = 0 | 1 | 2 | 3

export interface OrderItem {
  id: number
  productId: number
  productName: string
  price: number
  quantity: number
  imageUrl?: string
  spec?: string
}

export interface Order {
  id: number
  orderNo: string
  userId: number
  totalAmount: number
  status: OrderStatus
  address: string
  contact: string
  phone: string
  trackingNo: string | null
  createTime: string
  items: OrderItem[]
}

export interface CreateOrderItem {
  productId: number
  quantity: number
}

export interface CreateOrderRequest {
  items: CreateOrderItem[]
  address: string
  contact: string
  phone: string
}

export interface TrackingInfo {
  orderNo: string
  status: OrderStatus
  trackingNo: string | null
  logistics: LogisticsItem[]
}

export interface LogisticsItem {
  time: string
  content: string
  status: string
}

// 购物车相关类型
export interface CartItem {
  productId: number
  quantity: number
  product?: Product
}

// 统计数据类型
export interface AdminStats {
  totalUsers: number
  totalProducts: number
  totalOrders: number
  todaySales: number
  weekSales: number
  monthSales: number
  orderStatusStats: {
    pending: number
    processing: number
    shipped: number
    completed: number
  }
  recentOrders: Order[]
  hotProducts: {
    productId: number
    productName: string
    salesCount: number
  }[]
}

// 评论相关类型
export interface Comment {
  id: number
  productId: number
  userId: number
  content: string
  rating: number
  likes: number
  nickname: string
  createTime: string
  updateTime: string
}

export interface CreateCommentRequest {
  productId: number
  content: string
  rating: number
  nickname: string
}

// 收藏相关类型
export interface Favorite {
  id: number
  productId: number
  userId: number
  createTime: string
  updateTime: string
}

// ==================== API 模块 ====================

/**
 * 认证相关 API
 */
export const authApi = {
  /** 用户登录 */
  login: (data: LoginRequest): Promise<LoginResponse> =>
    api.post('/auth/login', data),

  /** 用户注册 */
  register: (data: RegisterRequest): Promise<LoginResponse> =>
    api.post('/auth/register', data),
}

/**
 * 用户相关 API
 */
export const userApi = {
  /** 获取当前用户信息 */
  getProfile: (): Promise<User> =>
    api.get('/user/profile'),

  /** 更新用户信息 */
  updateProfile: (data: Partial<User>): Promise<User> =>
    api.put('/user/profile', data),

  /** 修改密码 */
  changePassword: (oldPassword: string, newPassword: string): Promise<void> =>
    api.put('/user/password', { oldPassword, newPassword }),
}

/**
 * 商品相关 API
 */
export const productApi = {
  /** 获取所有商品 */
  getAll: (): Promise<Product[]> =>
    api.get('/products'),

  /** 获取新品上市商品 */
  getNew: (): Promise<Product[]> =>
    api.get('/products/new'),

  /** 获取热销排行商品 */
  getHot: (): Promise<Product[]> =>
    api.get('/products/hot'),

  /** 获取商品详情 */
  getById: (id: number): Promise<Product> =>
    api.get(`/products/${id}`),

  /** 根据分类获取商品 */
  getByCategory: (category: string): Promise<Product[]> =>
    api.get('/products', { params: { category } }),

  /** 搜索商品（分页） */
  search: (keyword: string, page = 0, size = 12): Promise<PageResult<Product>> =>
    api.get('/products/search', { params: { keyword, page, size } }),

  /** 创建商品（管理员） */
  create: (data: CreateProductRequest): Promise<Product> =>
    api.post('/products', data),

  /** 更新商品（管理员） */
  update: (id: number, data: UpdateProductRequest): Promise<Product> =>
    api.put(`/products/${id}`, data),

  /** 删除商品（管理员） */
  delete: (id: number): Promise<void> =>
    api.delete(`/products/${id}`),

  /** 更新商品上架状态（管理员） */
  toggleActive: (id: number, active: boolean): Promise<Product> =>
    api.patch(`/products/${id}/active`, { active }),
}

/**
 * 订单相关 API
 */
export const orderApi = {
  /** 获取我的订单列表 */
  getMyOrders: (): Promise<Order[]> =>
    api.get('/orders'),

  /** 创建订单 */
  create: (data: CreateOrderRequest): Promise<Order> =>
    api.post('/orders', data),

  /** 获取订单详情 */
  getById: (id: number): Promise<Order> =>
    api.get(`/orders/${id}`),

  /** 根据订单号查询 */
  getByOrderNo: (orderNo: string): Promise<Order> =>
    api.get(`/orders/tracking/${orderNo}`),

  /** 追踪订单物流 */
  track: (orderNo: string): Promise<TrackingInfo> =>
    api.get(`/orders/tracking/${orderNo}`),

  /** 取消订单 */
  cancel: (id: number): Promise<Order> =>
    api.post(`/orders/${id}/cancel`),

  /** 确认收货 */
  confirmReceive: (id: number): Promise<Order> =>
    api.post(`/orders/${id}/confirm`),

  /** 支付订单 */
  pay: (id: number): Promise<Order> =>
    api.post(`/orders/${id}/pay`),
}

/**
 * 购物车相关 API（本地存储为主，可扩展为服务端）
 */
export const cartApi = {
  /** 获取购物车商品详情列表 */
  getCartItems: async (items: CartItem[]): Promise<(CartItem & { product: Product })[]> => {
    const productIds = items.map(item => item.productId)
    const products = await api.post('/cart/items', { productIds }) as Product[]
    return items.map(item => ({
      ...item,
      product: products.find((p: Product) => p.id === item.productId)!
    }))
  },

  /** 校验库存 */
  validateStock: (items: CartItem[]): Promise<{ valid: boolean; invalidItems: number[] }> =>
    api.post('/cart/validate', { items }),
}

/**
 * 管理后台 API
 */
export const adminApi = {
  /** 获取统计数据 */
  getStats: (): Promise<AdminStats> =>
    api.get('/admin/stats'),

  getStores: (status?: number) => api.get('/admin/stores', { params: { status } }),
  auditStore: (id: number, pass: boolean, reason?: string) => 
    api.put(`/admin/store/${id}/audit`, { pass, reason }),

  /** 获取所有用户 */
  getUsers: (page = 0, size = 10): Promise<PageResult<User>> =>
    api.get('/admin/users', { params: { page, size } }),

  /** 获取所有订单 */
  getOrders: (page = 0, size = 10): Promise<PageResult<any>> =>
    api.get('/admin/orders', { params: { page, size } }),

  /** 订单发货 */
  shipOrder: (orderId: number, trackingNo: string): Promise<Order> =>
    api.post(`/admin/orders/${orderId}/ship`, { trackingNo }),

  /** 获取订单详情（管理员） */
  getOrderDetail: (orderId: number): Promise<Order> =>
    api.get(`/admin/orders/${orderId}`),

  /** 更新订单状态 */
  updateOrderStatus: (orderId: number, status: OrderStatus): Promise<Order> =>
    api.patch(`/admin/orders/${orderId}/status`, { status }),

  /** 获取销售报表 */
  getSalesReport: (params: {
    type: 'day' | 'week' | 'month'
    startDate: string
    endDate: string
  }): Promise<{
    labels: string[]
    sales: number[]
    orders: number[]
  }> =>
    api.get('/admin/reports/sales', { params }),

  /** 获取分类统计 */
  getCategoryStats: (): Promise<{
    category: string
    productCount: number
    salesCount: number
  }[]> =>
    api.get('/admin/reports/categories'),

  /** 创建用户 */
  createUser: (user: Partial<User>): Promise<User> =>
    api.post('/admin/users', user),

  /** 更新用户 */
  updateUser: (userId: number, user: Partial<User>): Promise<User> =>
    api.put(`/admin/users/${userId}`, user),

  /** 删除用户 */
  deleteUser: (userId: number): Promise<void> =>
    api.delete(`/admin/users/${userId}`),

  /** 更新用户状态 */
  updateUserStatus: (userId: number, active: boolean): Promise<User> =>
    api.patch(`/admin/users/${userId}/status?active=${active}`),

  /** 重置用户密码 */
  resetUserPassword: (userId: number): Promise<{ message: string; newPassword: string }> =>
    api.post(`/admin/users/${userId}/reset-password`),

  /** 获取操作日志 */
  getOperationLogs: (): Promise<any[]> =>
    api.get('/admin/logs'),
}

/**
 * 评论相关 API
 */
export const commentApi = {
  /** 获取商品评论 */
  getByProduct: (productId: number): Promise<Comment[]> =>
    api.get(`/comments/product/${productId}`),

  /** 获取用户评论 */
  getByUser: (userId: number): Promise<Comment[]> =>
    api.get(`/comments/user/${userId}`),

  /** 创建评论 */
  create: (data: CreateCommentRequest): Promise<Comment> =>
    api.post('/comments', data),

  /** 更新评论 */
  update: (id: number, data: Partial<Comment>): Promise<Comment> =>
    api.put(`/comments/${id}`, data),

  /** 删除评论 */
  delete: (id: number): Promise<void> =>
    api.delete(`/comments/${id}`),
}

/**
 * 收藏相关 API
 */
export const favoriteApi = {
  /** 获取用户收藏列表 */
  getUserFavorites: (userId: number): Promise<Favorite[]> =>
    api.get(`/favorites/user/${userId}`),

  /** 检查是否已收藏（通过userId） */
  checkFavorite: (userId: number, productId: number): Promise<boolean> =>
    api.get(`/favorites/check/${userId}/${productId}`),

  /** 检查是否已收藏（通过username） */
  checkFavoriteByUsername: (username: string, productId: number): Promise<boolean> =>
    api.get(`/favorites/check/username/${username}/${productId}`),

  /** 添加收藏 */
  add: (data: Omit<Favorite, 'id' | 'createTime' | 'updateTime'>): Promise<Favorite> =>
    api.post('/favorites', data),

  /** 取消收藏 */
  remove: (userId: number, productId: number): Promise<void> =>
    api.delete(`/favorites/${userId}/${productId}`),
}

/**
 * 店家相关 API
 */
export const storeApi = {
  /** 注册店铺 */
  register: (data: {
    storeName: string
    description?: string
    phone?: string
    address?: string
    logoUrl?: string
  }): Promise<any> =>
    api.post('/store/register', data),

  /** 获取我的店铺 */
  getMyStore: (): Promise<any> =>
    api.get('/store/my'),

  /** 更新店铺信息 */
  updateStore: (data: any): Promise<any> =>
    api.put('/store/my', data),

  /** 获取店铺商品列表 */
  getProducts: (params?: { 
    keyword?: string; 
    page?: number; 
    size?: number;
    status?: number;
    categoryId?: number;
    stockWarning?: boolean
  }): Promise<PageResult<any>> =>
    api.get('/store/product/list', { params }),

  /** 创建商品 */
  createProduct: (data: any): Promise<any> =>
    api.post('/store/product/create', data),

  /** 更新商品 */
  updateProduct: (id: number, data: any): Promise<any> =>
    api.put(`/store/product/${id}`, data),

  /** 删除商品 */
  deleteProduct: (id: number): Promise<void> =>
    api.delete(`/store/product/${id}`),

  /** 商品上下架 */
  toggleProduct: (id: number, active: boolean): Promise<any> =>
    api.put(`/store/product/${id}/active`, { active }),

  /** 获取库存日志 */
  getStockLogs: (productId: number): Promise<any[]> =>
    api.get(`/store/product/stock-logs/${productId}`),

  /** 获取库存预警 */
  getStockWarning: (): Promise<any[]> =>
    api.get('/store/product/warning'),

  /** 获取店铺订单列表 */
  getOrders: (): Promise<any[]> =>
    api.get('/store/order/list'),

  /** 订单发货 */
  shipOrder: (id: number, trackingNo: string): Promise<any> =>
    api.post(`/store/order/${id}/ship`, { trackingNo }),

  /** 上传图片 */
  uploadImage: (file: File): Promise<string> => {
    const formData = new FormData()
    formData.append('file', file)
    const userStore = useUserStore()
    return api.post('/store/product/upload', formData, {
      headers: { 
        'Content-Type': 'multipart/form-data',
        'Authorization': `Bearer ${userStore.token}`
      }
    })
  },
}

/**
 * 分类相关 API
 */
export const categoryApi = {
  /** 获取所有分类 */
  getAll: (): Promise<any[]> =>
    api.get('/category/list'),

  /** 获取分类树 */
  getTree: (): Promise<any[]> =>
    api.get('/category/tree'),

  /** 获取子分类 */
  getChildren: (parentId: number): Promise<any[]> =>
    api.get(`/category/children/${parentId}`),

  /** 创建分类（管理员） */
  create: (data: any): Promise<any> =>
    api.post('/category/create', data),

  /** 更新分类（管理员） */
  update: (id: number, data: any): Promise<any> =>
    api.put(`/category/update/${id}`, data),

  /** 删除分类（管理员） */
  delete: (id: number): Promise<void> =>
    api.delete(`/category/delete/${id}`),
}

/**
 * 管理员审核 API
 */
export const auditApi = {
  /** 获取店铺列表（含待审核） */
  getStores: (status?: number, page = 0, size = 10): Promise<PageResult<any>> =>
    api.get('/admin/stores', { params: { status, page, size } }),

  /** 审核店铺 */
  auditStore: (id: number, pass: boolean, reason?: string): Promise<any> =>
    api.put(`/admin/store/${id}/audit`, { pass, reason }),

  /** 获取商品列表（含待审核） */
  getProducts: (params?: {
    status?: number;
    page?: number;
    size?: number;
    keyword?: string;
    categoryId?: number;
  }): Promise<PageResult<any>> =>
    api.get('/admin/products', { params }),

  /** 审核商品 */
  auditProduct: (id: number, pass: boolean, reason?: string): Promise<any> =>
    api.put(`/admin/product/${id}/audit`, { pass, reason }),

  /** 创建商品 */
  createProduct: (data: any): Promise<any> =>
    api.post('/admin/product', data),

  /** 更新商品 */
  updateProduct: (id: number, data: any): Promise<any> =>
    api.put(`/admin/product/${id}`, data),

  /** 删除商品 */
  deleteProduct: (id: number): Promise<void> =>
    api.delete(`/admin/product/${id}`),

  /** 切换商品上下架 */
  toggleActive: (id: number, active: boolean): Promise<any> =>
    api.patch(`/admin/product/${id}/active`, { active }),

  /** 上传图片（管理员使用） */
  uploadImage: (file: File): Promise<string> => {
    const formData = new FormData()
    formData.append('file', file)
    const userStore = useUserStore()
    return api.post('/store/product/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
        'Authorization': `Bearer ${userStore.token}`
      }
    })
  },
}

// 默认导出 axios 实例
export default api
