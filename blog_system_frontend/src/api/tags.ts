import { http } from '@/utils/http'

export interface Tag {
  id: number
  name: string
  createAt: string
}

export interface TagCreateRequest {
  name: string
}

export interface TagUpdateRequest {
  id: number
  name: string
}

export const listTags = () => http.get<Tag[]>('/tags')

export const getTagById = (id: number) => http.get<Tag>(`/tags/${id}`)

export const createTag = (payload: TagCreateRequest) => http.post<Tag, TagCreateRequest>('/tags', payload)

export const updateTag = (id: number, payload: TagUpdateRequest) =>
  http.put<Tag, TagUpdateRequest>(`/tags/${id}`, payload)

export const deleteTag = (id: number) => http.delete<void>(`/tags/${id}`)
