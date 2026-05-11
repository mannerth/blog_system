import { http } from '@/utils/http'

export interface LikeResponse {
  like_count: number
}

// TODO: 后端待实现
export const likeBlog = (blogId: number) =>
  http.post<LikeResponse, undefined>(`/blogs/${blogId}/like`, undefined)

// TODO: 后端待实现
export const unlikeBlog = (blogId: number) =>
  http.delete<LikeResponse>(`/blogs/${blogId}/like`)

// TODO: 后端待实现
export const likeComment = (commentId: number) =>
  http.post<LikeResponse, undefined>(`/comments/${commentId}/like`, undefined)

// TODO: 后端待实现
export const unlikeComment = (commentId: number) =>
  http.delete<LikeResponse>(`/comments/${commentId}/like`)
