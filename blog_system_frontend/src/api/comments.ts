import { http } from '@/utils/http'
import type { PageResponse } from './types'

export interface Comment {
  id?: number
  blogId?: number
  userId?: number
  username?: string
  parentCommentId?: number | null
  content?: string
  likeCount?: number
  replies?: Comment[]
  createdAt?: string
  __liked?: boolean
}

export interface CommentRequest {
  content: string
}

export type CommentListParams = Record<string, string | number | boolean | null | undefined>

const normalizePageParams = (params?: CommentListParams) => {
  const normalized: Record<string, string | number | boolean | null | undefined> = {}
  if (params?.page !== undefined) {
    const pageValue = Number(params.page)
    if (Number.isFinite(pageValue)) {
      normalized.page = Math.max(0, pageValue)
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

export const listBlogComments = (blogId: number, params?: CommentListParams) =>
  http.get<PageResponse<Comment>>(`/blogs/${blogId}/comments`, { params: normalizePageParams(params) })

export const createBlogComment = (blogId: number, payload: CommentRequest) =>
  http.post<Comment, CommentRequest>(`/blogs/${blogId}/comments`, payload)

export const replyComment = (commentId: number, payload: CommentRequest) =>
  http.post<Comment, CommentRequest>(`/comments/${commentId}/replies`, payload)

export const deleteComment = (commentId: number) =>
  http.delete<void>(`/comments/${commentId}`)
