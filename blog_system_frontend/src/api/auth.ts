import { http } from '@/utils/http'
import { setToken } from '@/utils/auth'

export interface LoginRequest {
  username: string
  password: string
}

export interface RegisterRequest {
  username: string
  password: string
}

export interface AuthResponse {
  token: string
  tokenType?: string
  username: string
  role: 'USER' | 'ADMIN'
}

export const login = async (payload: LoginRequest) => {
  const data = await http.post<AuthResponse, LoginRequest>('/auth/login', payload, { withAuth: false })
  if (data?.token) setToken(data.token)
  return data
}

export const register = async (payload: RegisterRequest) => {
  const data = await http.post<AuthResponse, RegisterRequest>('/auth/register', payload, { withAuth: false })
  if (data?.token) setToken(data.token)
  return data
}
