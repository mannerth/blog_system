import { http } from '@/utils/http'

export interface UserProfile {
  id: number
  username: string
  role: 'USER' | 'ADMIN'
  createdAt: string
}

export interface UserCreateRequest {
  username: string
  password: string
  role?: 'USER' | 'ADMIN'
}

export interface UserUpdateRequest {
  username: string
  password?: string
  role?: 'USER' | 'ADMIN'
}

export const getMe = () => http.get<UserProfile>('/users/me')

export const listUsers = () => http.get<UserProfile[]>('/users')

export const getUserById = (id: number) => http.get<UserProfile>(`/users/${id}`)

export const createUser = (payload: UserCreateRequest) => http.post<UserProfile, UserCreateRequest>('/users', payload)

export const updateUser = (id: number, payload: UserUpdateRequest) =>
  http.put<UserProfile, UserUpdateRequest>(`/users/${id}`, payload)

export const deleteUser = (id: number) => http.delete<void>(`/users/${id}`)
