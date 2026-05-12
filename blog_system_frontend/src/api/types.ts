export type ApiUserRole = 'USER' | 'ADMIN'

export interface ApiUser {
  id?: number
  username?: string
  nickname?: string
  avatarUrl?: string
  bio?: string
  role?: ApiUserRole
  createdAt?: string
  updatedAt?: string
}

export interface Category {
  id?: number
  name?: string
  description?: string
  createAt?: string
}

export interface Tag {
  id?: number
  name?: string
  createAt?: string
}

export interface PageResult<T> {
  items: T[]
  total: number
  page: number
  size: number
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  number: number
  size: number
}
