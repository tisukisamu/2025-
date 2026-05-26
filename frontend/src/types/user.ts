export interface User {
  id: number
  username: string
  password?: string
  name: string
  email: string
  age: number
  avatarUrl?: string
  role: UserRole
  status: UserStatus
  createdAt?: string
  updatedAt?: string
}

export enum UserRole {
  ADMIN = 'ADMIN',
  USER = 'USER',
  COMPANY = 'COMPANY'
}

export enum UserStatus {
  ACTIVE = 'ACTIVE',
  DISABLED = 'DISABLED'
}

export interface LoginRequest {
  username: string
  password: string
}

export interface RegisterRequest {
  username: string
  password: string
  name: string
  email: string
  age: number
}

export interface UserDTO {
  username?: string
  password?: string
  name: string
  email: string
  age: number
  avatarUrl?: string
  role?: UserRole
  status?: UserStatus
}

export interface AuthResponse {
  token: string
  type: string
  id: number
  username: string
  email: string
  name: string
  age?: number
  avatarUrl?: string
  role: UserRole
  status: UserStatus
}
