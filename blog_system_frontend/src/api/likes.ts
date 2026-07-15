import { http } from '@/utils/http'

export const toggleBlogLike = (blogId: number) =>
  http.post<boolean, undefined>(`/blogs/${blogId}/like`, undefined)

export const toggleCommentLike = (commentId: number) =>
  http.post<boolean, undefined>(`/comments/${commentId}/like`, undefined)
