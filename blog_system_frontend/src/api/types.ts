export type ApiUserRole = 'user' | 'admin'

export interface ApiUser {
  user_id?: number
  username?: string
  nickname?: string
  avatar_url?: string
  bio?: string
  role?: ApiUserRole
  created_at?: string
  updated_at?: string
}

export interface Category {
  category_id?: number
  name?: string
  description?: string
  created_at?: string
}

export interface Tag {
  tag_id?: number
  name?: string
  created_at?: string
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
