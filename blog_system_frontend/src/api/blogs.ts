import { http } from '@/utils/http'
import type { ApiUser, Category, Tag, PageResult } from './types'

export interface Blog {
  blog_id?: number
  title?: string
  content?: string
  user_id?: number
  author?: ApiUser
  category?: Category
  tags?: Tag[]
  view_count?: number
  like_count?: number
  created_at?: string
  updated_at?: string
}

export interface BlogCreateRequest {
  title: string
  content: string
  category_id: number
  tags?: string[]
}

export interface BlogUpdateRequest {
  title?: string
  content?: string
  category_id?: number
  tags?: string[]
}

export type BlogListParams = Record<string, string | number | boolean | null | undefined>

// TODO: 后端待实现
export const listBlogs = (params?: BlogListParams) =>
  http.get<PageResult<Blog>>('/blogs', { params })

// TODO: 后端待实现
export const createBlog = (payload: BlogCreateRequest) =>
  http.post<Blog, BlogCreateRequest>('/blogs', payload)

// TODO: 后端待实现
export const getBlogDetail = (blogId: number) =>
  http.get<Blog>(`/blogs/${blogId}`)

// TODO: 后端待实现
export const updateBlog = (blogId: number, payload: BlogUpdateRequest) =>
  http.put<Blog, BlogUpdateRequest>(`/blogs/${blogId}`, payload)

// TODO: 后端待实现
export const deleteBlog = (blogId: number) =>
  http.delete<void>(`/blogs/${blogId}`)

// TODO: 后端待实现
export const listMyBlogs = (params?: { page?: number; size?: number }) =>
  http.get<PageResult<Blog>>('/users/me/blogs', { params })

// TODO: 后端待实现
export const listUserBlogs = (userId: number, params?: { page?: number; size?: number }) =>
  http.get<PageResult<Blog>>(`/users/${userId}/blogs`, { params })

// TODO: 后端待实现
export const listAdminBlogs = (params?: BlogListParams) =>
  http.get<PageResult<Blog>>('/admin/blogs', { params })

// TODO: 后端待实现
export const updateAdminBlog = (blogId: number, payload: BlogUpdateRequest) =>
  http.put<Blog, BlogUpdateRequest>(`/admin/blogs/${blogId}`, payload)

// TODO: 后端待实现
export const deleteAdminBlog = (blogId: number) =>
  http.delete<void>(`/admin/blogs/${blogId}`)
