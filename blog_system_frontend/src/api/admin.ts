import { http } from '@/utils/http'

export interface AdminPingResponse {
  message: string
}

export const pingAdmin = () => http.get<AdminPingResponse>('/admin/ping')
