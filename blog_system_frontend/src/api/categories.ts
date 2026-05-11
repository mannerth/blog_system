import { http } from '@/utils/http'
import type { Category } from './types'

export interface CategoryCreateRequest {
  name: string
  description?: string
}

export interface CategoryUpdateRequest {
  name?: string
  description?: string
}

// TODO: 后端待实现
export const listCategories = () => http.get<Category[]>('/categories')

// TODO: 后端待实现
export const createCategory = (payload: CategoryCreateRequest) =>
  http.post<Category, CategoryCreateRequest>('/categories', payload)

// TODO: 后端待实现
export const updateCategory = (categoryId: number, payload: CategoryUpdateRequest) =>
  http.put<Category, CategoryUpdateRequest>(`/categories/${categoryId}`, payload)

// TODO: 后端待实现
export const deleteCategory = (categoryId: number) =>
  http.delete<void>(`/categories/${categoryId}`)
