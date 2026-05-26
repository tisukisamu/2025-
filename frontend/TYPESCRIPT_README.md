# 前端 TypeScript 类型定义和 API 接口文档

## 📁 目录结构

```
frontend/src/
├── types/              # TypeScript 类型定义
│   ├── user.ts         # 用户相关类型
│   ├── teacher.ts      # 教师相关类型
│   ├── student.ts      # 学员相关类型
│   ├── course.ts       # 课程相关类型
│   ├── schedule.ts     # 排班相关类型
│   ├── enrollment.ts   # 报名相关类型
│   ├── payment.ts      # 支付相关类型
│   ├── bill.ts         # 账单相关类型
│   ├── notification.ts # 通知相关类型
│   ├── setting.ts      # 设置相关类型
│   ├── statistics.ts   # 统计相关类型
│   ├── common.ts       # 通用类型
│   └── index.ts        # 统一导出
├── api/                # API 接口
│   ├── auth.ts         # 认证相关API
│   ├── user.ts         # 用户相关API
│   ├── admin.ts        # 管理员相关API
│   ├── course.ts       # 课程相关API
│   ├── teacher.ts      # 教师相关API
│   ├── student.ts      # 学员相关API
│   ├── finance.ts      # 财务相关API
│   ├── statistics.ts   # 统计相关API
│   ├── settings.ts     # 设置相关API
│   └── index.ts        # 统一导出
└── utils/
    └── request.ts      # Axios 封装
```

## 🎯 使用示例

### 1. 导入类型

```typescript
import type { User, Course, Enrollment } from '@/types'
import { UserRole, CourseStatus, PaymentStatus } from '@/types'
```

### 2. 使用 API 接口

```typescript
import { login, register, getCurrentUser } from '@/api/auth'
import { getCourses, enrollCourse } from '@/api/course'
import type { LoginRequest, CourseRequest } from '@/types'

// 登录
const handleLogin = async () => {
  const credentials: LoginRequest = {
    username: 'admin',
    password: '123456'
  }
  const res = await login(credentials)
  if (res.code === 200) {
    console.log('登录成功', res.data)
  }
}

// 获取课程列表
const loadCourses = async () => {
  const res = await getCourses({ page: 1, size: 10 })
  if (res.code === 200) {
    console.log('课程列表', res.data.content)
  }
}
```

### 3. 在 Vue 组件中使用

```vue
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getCourses } from '@/api/course'
import type { Course } from '@/types'

const courses = ref<Course[]>([])
const loading = ref(false)

const loadCourses = async () => {
  loading.value = true
  try {
    const res = await getCourses({ page: 1, size: 10 })
    if (res.code === 200) {
      courses.value = res.data.content
    }
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadCourses()
})
</script>
```

### 4. 使用 Pinia Store

```typescript
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

// 登录
await userStore.loginAction({
  username: 'admin',
  password: '123456'
})

// 检查权限
if (userStore.checkPermission('ADMIN')) {
  // 执行管理员操作
}

// 获取用户信息
console.log(userStore.userInfo)
console.log(userStore.isAdmin)
console.log(userStore.realName)
```

## 📋 类型定义说明

### 用户相关类型

```typescript
// 用户角色枚举
enum UserRole {
  ADMIN = 'ADMIN',
  TEACHER = 'TEACHER',
  STUDENT = 'STUDENT'
}

// 用户状态枚举
enum UserStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  BANNED = 'BANNED'
}

// 用户接口
interface User {
  id: number
  username: string
  email?: string
  phone?: string
  realName?: string
  avatar?: string
  role: UserRole
  status: UserStatus
  createdAt: string
  updatedAt: string
}
```

### 课程相关类型

```typescript
// 课程等级枚举
enum CourseLevel {
  BEGINNER = 'BEGINNER',
  INTERMEDIATE = 'INTERMEDIATE',
  ADVANCED = 'ADVANCED'
}

// 课程状态枚举
enum CourseStatus {
  DRAFT = 'DRAFT',
  PUBLISHED = 'PUBLISHED',
  CLOSED = 'CLOSED'
}

// 课程接口
interface Course {
  id: number
  name: string
  description?: string
  teacher?: Teacher
  category?: string
  level?: CourseLevel
  duration?: number
  price: number
  capacity: number
  enrolledCount: number
  image?: string
  status: CourseStatus
  createdAt: string
  updatedAt: string
}
```

### 通用响应类型

```typescript
// API 响应接口
interface ApiResponse<T = any> {
  code: number
  message: string
  data?: T
}

// 分页响应接口
interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  first: boolean
  last: boolean
  empty: boolean
}

// 分页请求接口
interface PageRequest {
  page?: number
  size?: number
  sort?: string
  direction?: 'ASC' | 'DESC'
}
```

## 🔧 配置说明

### tsconfig.json 配置

确保 `tsconfig.json` 中包含以下配置：

```json
{
  "compilerOptions": {
    "target": "ES2020",
    "useDefineForClassFields": true,
    "module": "ESNext",
    "lib": ["ES2020", "DOM", "DOM.Iterable"],
    "skipLibCheck": true,
    "moduleResolution": "bundler",
    "allowImportingTsExtensions": true,
    "resolveJsonModule": true,
    "isolatedModules": true,
    "noEmit": true,
    "jsx": "preserve",
    "strict": true,
    "noUnusedLocals": true,
    "noUnusedParameters": true,
    "noFallthroughCasesInSwitch": true,
    "paths": {
      "@/*": ["./src/*"]
    }
  },
  "include": ["src/**/*.ts", "src/**/*.tsx", "src/**/*.vue"],
  "references": [{ "path": "./tsconfig.node.json" }]
}
```

### vite.config.ts 配置

确保 `vite.config.ts` 中包含路径别名：

```typescript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  }
})
```

## 🚀 最佳实践

1. **始终使用类型导入**: 使用 `import type` 导入类型定义
2. **API 响应处理**: 统一使用 `ApiResponse` 类型处理响应
3. **错误处理**: 使用 try-catch 处理 API 调用错误
4. **类型安全**: 充分利用 TypeScript 的类型检查功能
5. **代码复用**: 通过类型继承和组合提高代码复用率

## 📝 注意事项

1. 所有 API 接口都返回 `Promise<ApiResponse<T>>` 类型
2. 枚举类型需要使用 `export enum` 导出才能在其他文件中使用
3. 可选属性使用 `?` 标记,表示该属性可能为 `undefined`
4. 分页查询使用 `PageRequest` 和 `PageResponse` 类型
5. 日期时间字段统一使用 `string` 类型 (ISO 8601 格式)
