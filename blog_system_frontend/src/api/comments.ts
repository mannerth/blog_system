import { http } from '@/utils/http'
import type { ApiUser, PageResult } from './types'

export interface Comment {
  comment_id?: number
  blog_id?: number
  user?: ApiUser
  parent_comment_id?: number | null
  content?: string
  like_count?: number
  replies?: Comment[]
  created_at?: string
}

export interface CommentRequest {
  content: string
}

export type CommentListParams = Record<string, string | number | boolean | null | undefined>

// TODO: 后端待实现
export const listBlogComments = (blogId: number, params?: CommentListParams) =>
  http.get<PageResult<Comment>>(`/blogs/${blogId}/comments`, { params })

// TODO: 后端待实现
export const createBlogComment = (blogId: number, payload: CommentRequest) =>
  http.post<Comment, CommentRequest>(`/blogs/${blogId}/comments`, payload)

// TODO: 后端待实现
export const replyComment = (commentId: number, payload: CommentRequest) =>
  http.post<Comment, CommentRequest>(`/comments/${commentId}/reply`, payload)

// TODO: 后端待实现
export const deleteComment = (commentId: number) =>
  http.delete<void>(`/comments/${commentId}`)
