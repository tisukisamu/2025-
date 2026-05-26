# 前端详细设计说明书

## 1. 界面设计规范
- **UI 组件**: 统一使用 Ant Design Vue。
- **布局**: 
  - 前台：顶部导航 + 通栏内容区。
  - 后台：左侧侧边栏 + 顶部面包屑 + 内容主体。
- **响应式**: 使用 UnoCSS 的断点（如 `sm:`, `md:`, `lg:`）适配手机与电脑端。

## 2. 项目文件结构
```
frontend/src/
├── api/
│   └── index.ts              # API 接口封装
├── components/               # 公共组件
│   ├── CartItem.vue          # 购物车项组件
│   ├── CartSummary.vue       # 购物车结算组件
│   └── FlyToCart.vue         # 飞入购物车动画组件
├── router/
│   └── index.ts              # 路由配置
├── stores/
│   ├── user.ts               # 用户状态管理
│   └── cart.ts               # 购物车状态管理
├── styles/                   # 样式文件
│   ├── design-system.css     # 设计系统样式
│   └── theme.css             # 主题样式
├── utils/                    # 工具函数
│   └── adminSettings.ts      # 管理员设置
├── views/                    # 页面视图
│   ├── Home.vue              # 首页
│   ├── Login.vue             # 登录/注册
│   ├── ProductList.vue       # 商品列表
│   ├── ProductDetail.vue     # 商品详情
│   ├── ProductSearch.vue     # 商品搜索
│   ├── NewProducts.vue       # 新品上市
│   ├── HotProducts.vue       # 热销排行
│   ├── Cart.vue              # 购物车
│   ├── Orders.vue            # 我的订单
│   ├── OrderTracking.vue     # 订单追踪
│   ├── MyFavorites.vue       # 我的收藏
│   ├── StoreRegister.vue     # 店铺注册
│   ├── admin/                # 管理后台
│   │   ├── Layout.vue        # 后台布局
│   │   ├── Dashboard.vue     # 数据大屏
│   │   ├── Products.vue      # 商品管理
│   │   ├── Orders.vue        # 订单管理
│   │   ├── Users.vue         # 用户管理
│   │   ├── StoreAudit.vue    # 店铺审核
│   │   └── ProductAudit.vue  # 商品审核
│   └── store/                # 商家端
│       ├── StoreLayout.vue   # 店铺布局
│       ├── StoreDashboard.vue # 店铺概览
│       ├── StoreProducts.vue # 商品管理
│       ├── StoreOrders.vue   # 订单管理
│       ├── StoreStats.vue    # 数据统计
│       └── StoreSettings.vue # 店铺设置
├── App.vue
└── main.ts
```

## 3. 核心路由挂载

### 3.1 用户端 (User Side)
| 路由 | 页面 | 说明 |
|------|------|------|
| `/` | Home | 首页，展示商品列表 |
| `/login` | Login | 登录/注册切换 |
| `/product/list` | ProductList | 商品列表与筛选 |
| `/product/:id` | ProductDetail | 商品详情展示 |
| `/product/new` | NewProducts | 新品上市 |
| `/product/hot` | HotProducts | 热销排行 |
| `/search` | ProductSearch | 商品搜索 |
| `/cart` | Cart | 购物车页面 |
| `/orders` | Orders | 我的订单 |
| `/order/tracking` | OrderTracking | 物流追踪 |
| `/favorites` | MyFavorites | 我的收藏 |
| `/store/register` | StoreRegister | 申请开店 |

### 3.2 管理端 (Admin Side)
| 路由 | 页面 | 说明 |
|------|------|------|
| `/admin/dashboard` | Dashboard | 数据大屏 |
| `/admin/products` | Products | 商品管理 |
| `/admin/orders` | Orders | 订单管理 |
| `/admin/users` | Users | 用户管理 |
| `/admin/store-audit` | StoreAudit | 店铺入驻审核 |
| `/admin/product-audit` | ProductAudit | 商品审核 |

### 3.3 商家端 (Store Side)
| 路由 | 页面 | 说明 |
|------|------|------|
| `/store/dashboard` | StoreDashboard | 店铺运营数据概览 |
| `/store/products` | StoreProducts | 自营商品管理 |
| `/store/orders` | StoreOrders | 自营订单处理 |
| `/store/stats` | StoreStats | 数据统计 |
| `/store/settings` | StoreSettings | 店铺信息设置 |

## 4. 状态管理 (Pinia)

### 4.1 user.ts - 用户信息及 Token
```typescript
const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userId = ref(0)
  const username = ref('')
  const nickname = ref('')
  const role = ref('')
  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => role.value === 'ROLE_ADMIN')
  const isStore = computed(() => role.value === 'ROLE_STORE')
  
  function setUser(userData: LoginResponse)
  function logout()
})
```

### 4.2 cart.ts - 购物车列表，支持持久化
```typescript
const useCartStore = defineStore('cart', () => {
  const items = ref<CartItem[]>(JSON.parse(localStorage.getItem('cart') || '[]'))
  const totalItems = computed(() => items.value.reduce((sum, item) => sum + item.quantity, 0))
  const totalPrice = computed(() => ...)
  
  function addProduct(product: Product, quantity: number)
  function removeItem(productId: number)
  function updateQuantity(productId: number, quantity: number)
  function clearCart()
  function getItemsForOrder(): { productId: number; quantity: number }[]
})
```

## 5. API 服务封装

### 5.1 Axios 拦截器配置
- **请求拦截器**: 自动添加 JWT Token
- **响应拦截器**: 401 自动跳转登录页，统一错误处理

### 5.2 API 模块概览
```typescript
// 认证相关
export const authApi = { login, register }

// 用户相关
export const userApi = { getProfile, updateProfile, changePassword }

// 商品相关
export const productApi = { getAll, getById, getNew, getHot, search, create, update, delete, toggleActive }

// 分类相关
export const categoryApi = { getAll, getTree, getChildren, create, update, delete }

// 订单相关
export const orderApi = { getMyOrders, create, getById, cancel, confirmReceive, track }

// 收藏相关
export const favoriteApi = { getUserFavorites, checkFavorite, add, remove }

// 评论相关
export const commentApi = { getByProduct, getByUser, create, update, delete }

// 店铺相关
export const storeApi = { register, getMyStore, updateStore, getProducts, createProduct, updateProduct, deleteProduct, ... }

// 管理后台
export const adminApi = { getStats, getUsers, getOrders, shipOrder, updateOrderStatus, ... }

// 审核相关
export const auditApi = { getStores, auditStore, getProducts, auditProduct, ... }
```

### 5.3 类型定义
```typescript
// 订单状态
export type OrderStatus = 1 | 2 | 3
// 1: 待发货
// 2: 已发货
// 3: 已完成

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

export interface Favorite {
  id: number
  productId: number
  userId: number
  createTime: string
  updateTime: string
}
```

## 6. UnoCSS 配置

### 6.1 uno.config.ts
```typescript
import { defineConfig, presetUno, presetAttributify, presetIcons } from 'unocss'

export default defineConfig({
  presets: [
    presetUno(),
    presetAttributify(),
    presetIcons(),
  ],
  shortcuts: {
    'flex-center': 'flex items-center justify-center',
    'btn-primary': 'px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors'
  }
})
```

### 6.2 使用示例
```html
<!-- 商品卡片 -->
<div class="p-4 m-2 rounded-lg shadow-md hover:shadow-xl transition-all border-1 border-gray-100">
  <img src="..." class="w-full aspect-square object-cover" />
  <div class="mt-2 text-lg font-bold text-green-600">￥{{ price }}</div>
</div>

<!-- 使用 shortcuts -->
<button class="btn-primary">提交</button>
<div class="flex-center h-full">居中内容</div>
```

## 7. 路由守卫

### 7.1 权限控制
```typescript
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
```

### 7.2 路由元信息
- `requiresAuth`: 需要登录
- `requiresAdmin`: 需要管理员权限

## 8. 组件设计

### 8.1 首页 (Home.vue)
- 顶部导航栏（Logo、搜索框、购物车、用户信息）
- 分类导航
- 商品网格展示
- 新品/热销推荐
- 加入购物车功能

### 8.2 登录页 (Login.vue)
- Tabs 切换登录/注册
- 表单验证
- 登录后存储 Token 和用户信息

### 8.3 商品详情 (ProductDetail.vue)
- 商品图片轮播
- 商品信息展示
- 收藏按钮
- 加入购物车
- 商品评论列表

### 8.4 购物车 (Cart.vue)
- 商品列表表格
- 数量调整
- 总价计算
- 结算流程

**结算流程**:
1. 点击"立即结算"
2. 调用 `orderApi.create()` 创建订单
3. 清空购物车，跳转到订单列表

### 8.5 订单列表 (Orders.vue)
- 订单卡片列表展示
- 状态标签显示（待发货、已发货、已完成）
- 订单详情查看
- 确认收货按钮（已发货状态）

### 8.6 我的收藏 (MyFavorites.vue)
- 收藏商品列表
- 取消收藏
- 快速加入购物车
- 跳转商品详情

### 8.7 管理后台布局 (admin/Layout.vue)
- 左侧菜单栏
- 顶部面包屑
- 内容区域

### 8.8 店铺审核 (admin/StoreAudit.vue)
- 店铺列表展示
- 状态筛选（全部/待审核/已通过/已驳回）
- 审核通过/驳回操作
- 驳回原因填写

### 8.9 商品审核 (admin/ProductAudit.vue)
- 商品列表展示
- 状态筛选
- 审核通过/驳回操作
- 商品详情预览

### 8.10 店铺数据统计 (store/StoreStats.vue)
- 时间范围筛选（近7天/30天/90天）
- 销售趋势图表
- 分类占比图表
- 热销商品TOP10

## 9. 订单流程详细设计

### 9.1 流程图
```
用户点击结算
    ↓
验证购物车不为空
    ↓
调用 orderApi.create() 创建订单
    ↓
订单状态 = 1 (待发货)
    ↓
清空购物车
    ↓
跳转到订单列表页面
    ↓
商家发货
    ↓
订单状态 = 2 (已发货)
    ↓
用户确认收货
    ↓
订单状态 = 3 (已完成)
```

### 9.2 状态管理
```typescript
// Cart.vue
const handleCheckout = async () => {
  const orderData = {
    items: cartStore.getItemsForOrder(),
    address: userAddress.value,
    contact: userContact.value,
    phone: userPhone.value
  }
  const order = await orderApi.create(orderData)
  message.success('订单创建成功')
  cartStore.clearCart()
  router.push('/orders')
}
```

### 9.3 错误处理
- 库存不足：创建订单时检查，返回错误提示
- 订单不存在：操作时检查，返回错误提示
- 订单状态错误：操作时检查订单状态
- 网络错误：统一捕获并提示用户

## 10. 环境配置

### 10.1 vite.config.ts
```typescript
export default defineConfig({
  plugins: [vue(), UnoCSS()],
  server: {
    port: 5174,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

### 10.2 开发环境
- 开发服务器: `npm run dev`
- 端口: 5174
- 代理: `/api` -> `http://localhost:8080`

## 11. 依赖列表

### 11.1 生产依赖
- `vue`: ^3.4.15
- `vue-router`: ^4.2.5
- `pinia`: ^2.1.7
- `ant-design-vue`: ^4.1.2
- `axios`: ^1.6.7
- `@ant-design/icons-vue`: ^7.0.1

### 11.2 开发依赖
- `vite`: ^5.0.12
- `@vitejs/plugin-vue`: ^5.0.3
- `unocss`: ^0.58.5
- `typescript`: ^5.2.2
- `vue-tsc`: ^1.8.27

## 12. 新增功能说明

### 12.1 收藏功能
- 商品详情页可添加/取消收藏
- 我的收藏页面展示所有收藏商品
- 支持快速加入购物车

### 12.2 评论功能
- 商品详情页展示评论列表
- 登录用户可发表评论
- 支持评分（1-5星）

### 12.3 分类功能
- 支持多级分类
- 分类树展示
- 按分类筛选商品

### 12.4 店铺功能
- 用户可申请开店
- 店铺审核流程
- 店铺商品管理
- 店铺订单管理
- 库存预警

### 12.5 审核功能
- 店铺入驻审核
- 商品上架审核
- 审核状态追踪
- 驳回原因反馈
