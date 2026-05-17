import { http } from '@/utils/http'
import type { PageResponse } from './types'

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

const normalizePageParams = (params?: { page?: number; size?: number }) => {
  const normalized: Record<string, string | number | boolean | null | undefined> = {}
  if (params?.page !== undefined) {
    const pageValue = Number(params.page)
    if (Number.isFinite(pageValue)) {
      normalized.page = Math.max(0, pageValue - 1)
    }
  }
  if (params?.size !== undefined) {
    const sizeValue = Number(params.size)
    if (Number.isFinite(sizeValue)) {
      normalized.size = Math.max(1, sizeValue)
    }
  }
  return normalized
}

// TODO: 后端待实现
export const listBlogs = (params?: BlogListParams) => {
  const normalized: Record<string, string | number | boolean | null | undefined> = {
    ...normalizePageParams(params),
    keyword: params?.keyword,
  }
  if (params?.category_id !== undefined && params?.category_id !== '') {
    const categoryId = Number(params.category_id)
    if (!Number.isNaN(categoryId)) {
      normalized.categoryId = categoryId
    }
  }
  if (params?.tag !== undefined && params?.tag !== '') {
    const tagId = Number(params.tag)
    if (!Number.isNaN(tagId)) {
      normalized.tagId = tagId
    }
  }
  if (params?.sort) {
    normalized.sort = params.sort
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
  http.get<PageResponse<Blog>>('/users/me/blogs', { params: normalizePageParams(params) })

// TODO: 后端待实现
export const listUserBlogs = (userId: number, params?: { page?: number; size?: number }) =>
  http.get<PageResponse<Blog>>(`/users/${userId}/blogs`, { params: normalizePageParams(params) })

// TODO: 后端待实现
export const listAdminBlogs = (params?: BlogListParams) =>
  http.get<PageResponse<Blog>>('/admin/blogs', {
    params: {
      ...normalizePageParams(params),
      categoryId:
        params?.category_id !== undefined && params?.category_id !== ''
          ? Number(params.category_id)
          : undefined,
      tagId: params?.tag !== undefined && params?.tag !== '' ? Number(params.tag) : undefined,
      keyword: params?.keyword,
      sort: params?.sort,
    },
  })

// TODO: 后端待实现
export const updateAdminBlog = (blogId: number, payload: BlogUpdateRequest) =>
  http.put<Blog, {
    title?: string
    content?: string
    categoryId?: number
    tagIds?: number[]
  }>(`/admin/blogs/${blogId}`, {
    title: payload.title,
    content: payload.content,
    categoryId: payload.category_id,
    tagIds: payload.tags
      ?.map((tag) => Number(tag))
      .filter((value) => !Number.isNaN(value)),
  })

// TODO: 后端待实现
export const deleteAdminBlog = (blogId: number) =>
  http.delete<void>(`/admin/blogs/${blogId}`)

export const recordBlogView = (blogId: number) =>
  http.post<number, void>(`/blogs/${blogId}/view`)
