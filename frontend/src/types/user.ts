export interface User {
  id: number
  username: string
  name: string
  email: string
  age: number
  role: 'ADMIN' | 'USER'
  status: 'ACTIVE' | 'DISABLED'
  avatar?: string
  createdAt: string
  updatedAt: string
}

export interface LoginRequest {
  username: string
  password: string
}

export interface RegisterRequest {
  username: string
  password: string
  name: string
  email?: string
  age: number
}

export interface AuthResponse {
  token: string
  user: User
}
