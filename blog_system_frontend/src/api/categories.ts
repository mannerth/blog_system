import { http } from '@/utils/http'
export interface CategoryResponse {
  id: number
  name: string
  description?: string
  createAt: string
}

export interface CategoryCreateRequest {
  name: string
  description?: string
}

export interface CategoryUpdateRequest {
  name?: string
  description?: string
}

export const listCategories = () => http.get<CategoryResponse[]>('/categories')

// TODO: 后端待实现
export const createCategory = (payload: CategoryCreateRequest) =>
  http.post<CategoryResponse, CategoryCreateRequest>('/categories', payload)

// TODO: 后端待实现
export const updateCategory = (categoryId: number, payload: CategoryUpdateRequest) =>
  http.put<CategoryResponse, CategoryUpdateRequest>(`/categories/${categoryId}`, payload)

// TODO: 后端待实现
export const deleteCategory = (categoryId: number) =>
  http.delete<void>(`/categories/${categoryId}`)
