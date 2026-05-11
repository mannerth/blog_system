import { http } from '@/utils/http'
import type { PageResult } from './types'

export interface Blog {
  id?: number
  title?: string
  content?: string
  userId?: number
  username?: string
  categoryId?: number
  categoryName?: string
  tagNames?: string[]
  viewCount?: number
  likeCount?: number
  createAt?: string
  updatedAt?: string
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

export interface BlogListParams {
  page?: number
  size?: number
  category_id?: string | number
  tag?: string | number
  keyword?: string
  sort?: string
}

// TODO: 后端待实现
export const listBlogs = (params?: BlogListParams) => {
  const normalized: Record<string, string | number | boolean | null | undefined> = {
    page: params?.page,
    size: params?.size,
    keyword: params?.keyword,
    sort: params?.sort,
  }
  if (params?.category_id) {
    normalized.categoryId = Number(params.category_id)
  }
  if (params?.tag) {
    normalized.tagId = Number(params.tag)
  }
  return http.get<PageResult<Blog>>('/blogs', { params: normalized })
}

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
  http.get<PageResult<Blog>>('/admin/blogs', { params: params as Record<string, string | number | boolean | null | undefined> })

// TODO: 后端待实现
export const updateAdminBlog = (blogId: number, payload: BlogUpdateRequest) =>
  http.put<Blog, BlogUpdateRequest>(`/admin/blogs/${blogId}`, payload)

// TODO: 后端待实现
export const deleteAdminBlog = (blogId: number) =>
  http.delete<void>(`/admin/blogs/${blogId}`)
