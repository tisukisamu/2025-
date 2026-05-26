export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
  timestamp: number
}

export interface PageResponse<T> {
  list: T[]
  total: number
  page: number
  pageSize: number
  totalPages: number
}

export interface User {
  id: number
  username: string
  studentId?: string
  realName?: string
  phone?: string
  avatar?: string
  role: 'USER' | 'ADMIN'
  status: 'ACTIVE' | 'INACTIVE' | 'BANNED'
  creditScore: number
  createTime: string
}

export interface Category {
  id: number
  name: string
  parentId?: number
  sortOrder: number
  children?: Category[]
  createTime: string
}

export interface Product {
  id: number
  title: string
  description?: string
  price: number
  originalPrice?: number
  categoryId?: number
  categoryName?: string
  sellerId: number
  sellerName: string
  sellerAvatar?: string
  status: 'DRAFT' | 'PENDING' | 'ON_SALE' | 'OFF_SHELF' | 'SOLD'
  auditStatus: 'PENDING' | 'APPROVED' | 'REJECTED'
  auditReason?: string
  viewCount: number
  favoriteCount: number
  tradeType: 'FACE_TO_FACE' | 'EXPRESS' | 'BOTH'
  imageUrls?: string[]
  coverImage?: string
  createTime: string
  updateTime: string
}

export interface ProductImage {
  id: number
  productId: number
  imageUrl: string
  sortOrder: number
  createTime: string
}

export interface Order {
  id: number
  orderNo: string
  buyerId: number
  buyerName: string
  buyerAvatar?: string
  sellerId: number
  sellerName: string
  sellerAvatar?: string
  productId: number
  productTitle: string
  productImage?: string
  status: 'PENDING' | 'SHIPPED' | 'COMPLETED' | 'CANCELLED'
  tradeType: 'FACE_TO_FACE' | 'EXPRESS'
  amount: number
  address?: string
  expressNo?: string
  cancelReason?: string
  shipTime?: string
  completeTime?: string
  createTime: string
}

export interface Review {
  id: number
  orderId: number
  userId: number
  rating: number
  content?: string
  images?: string
  isAnonymous: boolean
  createTime: string
}

export interface Message {
  id: number
  senderId: number
  receiverId: number
  content: string
  type: 'TEXT' | 'IMAGE'
  isRead: boolean
  createTime: string
}

export interface Favorite {
  id: number
  productId: number
  productTitle: string
  productCoverImage?: string
  productPrice: number
  productStatus: string
  createTime: string
}

export interface Notification {
  id: number
  userId: number
  title: string
  content?: string
  type: 'SYSTEM' | 'ORDER' | 'REVIEW'
  relatedId?: number
  isRead: boolean
  createTime: string
}

export interface AuthResponse {
  token: string
  type: string
  id: number
  username: string
  studentId?: string
  realName?: string
  avatar?: string
  role: 'USER' | 'ADMIN'
  status: 'ACTIVE' | 'INACTIVE' | 'BANNED'
}

export interface LoginRequest {
  username: string
  password: string
}

export interface RegisterRequest {
  username: string
  password: string
  studentId: string
  realName?: string
  phone?: string
}

export interface ProductRequest {
  title: string
  description?: string
  price: number
  originalPrice?: number
  categoryId?: number
  tradeType: 'FACE_TO_FACE' | 'EXPRESS' | 'BOTH'
  imageUrls?: string[]
}

export interface OrderRequest {
  productId: number
  tradeType: 'FACE_TO_FACE' | 'EXPRESS'
  address?: string
}

export interface ReviewRequest {
  orderId: number
  rating: number
  content?: string
  images?: string
  isAnonymous?: boolean
}

export interface MessageRequest {
  receiverId: number
  content: string
  type?: 'TEXT' | 'IMAGE'
}

export interface UpdateUserRequest {
  nickname?: string
  realName?: string
  phone?: string
  avatar?: string
  bio?: string
  school?: string
}

export interface ChangePasswordRequest {
  oldPassword: string
  newPassword: string
}

export interface ProductQueryParams {
  keyword?: string
  categoryId?: number
  minPrice?: number
  maxPrice?: number
  tradeType?: string
  status?: string
  sellerId?: number
  sortBy?: string
  sortOrder?: 'asc' | 'desc'
  page?: number
  size?: number
}

export interface DashboardStats {
  totalUsers: number
  totalProducts: number
  pendingProducts: number
  totalOrders: number
  pendingOrders: number
  completedOrders: number
}
