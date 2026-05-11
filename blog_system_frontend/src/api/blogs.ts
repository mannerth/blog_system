import { http } from '@/utils/http'
import type { PageResponse, PageResult } from './types'

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
  tags?: Array<string | number>
}

export interface BlogUpdateRequest {
  title?: string
  content?: string
  category_id?: number
  tags?: Array<string | number>
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
  }
  if (params?.category_id) {
    normalized.categoryId = Number(params.category_id)
  }
  if (params?.tag) {
    normalized.tagId = Number(params.tag)
  }
  if (params?.sort) {
    const [property, direction] = params.sort.split(',')
    normalized.sort = property
    if (direction) {
      normalized.direction = direction.toUpperCase()
    }
  }
  return http.get<PageResponse<Blog>>('/blogs', { params: normalized })
}

// TODO: 后端待实现
export const createBlog = (payload: BlogCreateRequest) => {
  const normalized = {
    title: payload.title,
    content: payload.content,
    categoryId: payload.category_id,
    tagIds: payload.tags
      ?.map((tag) => Number(tag))
      .filter((value) => !Number.isNaN(value)),
  }
  return http.post<Blog, typeof normalized>('/blogs', normalized)
}

// TODO: 后端待实现
export const getBlogDetail = (blogId: number) =>
  http.get<Blog>(`/blogs/${blogId}`)

// TODO: 后端待实现
export const updateBlog = (blogId: number, payload: BlogUpdateRequest) => {
  const normalized = {
    title: payload.title,
    content: payload.content,
    categoryId: payload.category_id,
    tagIds: payload.tags
      ?.map((tag) => Number(tag))
      .filter((value) => !Number.isNaN(value)),
  }
  return http.put<Blog, typeof normalized>(`/blogs/${blogId}`, normalized)
}

// TODO: 后端待实现
export const deleteBlog = (blogId: number) =>
  http.delete<void>(`/blogs/${blogId}`)

// TODO: 后端待实现
export const listMyBlogs = (params?: { page?: number; size?: number }) =>
  http.get<PageResponse<Blog>>('/users/me/blogs', { params })

// TODO: 后端待实现
export const listUserBlogs = (userId: number, params?: { page?: number; size?: number }) =>
  http.get<PageResponse<Blog>>(`/users/${userId}/blogs`, { params })

// TODO: 后端待实现
export const listAdminBlogs = (params?: BlogListParams) =>
  http.get<PageResponse<Blog>>('/admin/blogs', { params: params as Record<string, string | number | boolean | null | undefined> })

// TODO: 后端待实现
export const updateAdminBlog = (blogId: number, payload: BlogUpdateRequest) =>
  http.put<Blog, BlogUpdateRequest>(`/admin/blogs/${blogId}`, payload)

// TODO: 后端待实现
export const deleteAdminBlog = (blogId: number) =>
  http.delete<void>(`/admin/blogs/${blogId}`)
