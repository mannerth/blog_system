<template>
  <section class="blog-detail">
    <header class="blog-detail__header">
      <div>
        <p class="blog-detail__eyebrow">Blog</p>
        <h1>{{ blog?.title || '加载中' }}</h1>
        <p class="blog-detail__meta">
          <span>{{ blog?.username || '佚名' }}</span>
          <span class="dot"></span>
          <span>{{ blog?.categoryName || '未分类' }}</span>
          <span class="dot"></span>
          <span>{{ createdAt }}</span>
        </p>
      </div>
      <BaseButton variant="outline" :loading="liking" @click="toggleLike">
        {{ likeText }}
      </BaseButton>
    </header>

    <div v-if="loading" class="blog-detail__state">
      <LoadingState title="加载中" description="正在获取文章详情" />
    </div>
    <div v-else-if="!blog" class="blog-detail__state">
      <EmptyState title="未找到博客" description="可能已被删除或不存在。" />
    </div>
    <div v-else class="blog-detail__content">
      <div v-if="blog.tagNames?.length" class="blog-detail__tags">
        <BaseTag v-for="tag in blog.tagNames" :key="tag" variant="outline">
          {{ tag }}
        </BaseTag>
      </div>
      <MyQuillEditor :content="contentDelta" read />
    </div>

    <CommentList
      v-if="blog"
      :comments="comments"
      :loading="commentsLoading"
      v-model="commentDraft"
      :error="commentError"
      :submitting="commentSubmitting"
      @submit="submitComment"
      @refresh="fetchComments"
    >
      <template #actions="{ comment }">
        <BaseButton variant="ghost" @click="openReply(comment)">回复</BaseButton>
        <BaseButton variant="outline" @click="toggleCommentLike(comment)">
          {{ comment.__liked ? '已赞' : '点赞' }} {{ comment.likeCount ?? 0 }}
        </BaseButton>
        <BaseButton v-if="canDelete(comment)" variant="ghost" @click="openDelete(comment)">
          删除
        </BaseButton>
      </template>
    </CommentList>

    <BaseModal v-model="replyOpen" title="回复评论" eyebrow="Reply">
      <BaseTextarea v-model="replyDraft" placeholder="输入回复内容" :error="replyError" clearable />
      <template #footer>
        <BaseButton variant="outline" @click="replyOpen = false">取消</BaseButton>
        <BaseButton :loading="replySubmitting" @click="submitReply">发送回复</BaseButton>
      </template>
    </BaseModal>

    <BaseModal v-model="deleteOpen" title="确认删除" eyebrow="Danger">
      <p>确定要删除这条评论吗？此操作不可撤销。</p>
      <template #footer>
        <BaseButton variant="outline" @click="deleteOpen = false">取消</BaseButton>
        <BaseButton variant="ghost" :loading="deleteSubmitting" @click="confirmDelete">确认删除</BaseButton>
      </template>
    </BaseModal>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import BaseButton from '@/components/base/BaseButton.vue'
import BaseModal from '@/components/base/BaseModal.vue'
import BaseTextarea from '@/components/base/BaseTextarea.vue'
import BaseTag from '@/components/base/BaseTag.vue'
import EmptyState from '@/components/base/EmptyState.vue'
import LoadingState from '@/components/base/LoadingState.vue'
import MyQuillEditor from '@/components/MyQuillEditor.vue'
import CommentList from '@/components/CommentList.vue'
import { getBlogDetail } from '@/api/blogs'
import { likeBlog, unlikeBlog } from '@/api/likes'
import { createBlogComment, deleteComment, listBlogComments, replyComment, type Comment } from '@/api/comments'
import { likeComment, unlikeComment } from '@/api/likes'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const blogId = computed(() => Number(route.params.id))

const loading = ref(false)
const liking = ref(false)
const blog = ref<Awaited<ReturnType<typeof getBlogDetail>> | null>(null)
const liked = ref(false)
const authStore = useAuthStore()

const comments = ref<Comment[]>([])
const commentsLoading = ref(false)
const commentDraft = ref('')
const commentError = ref('')
const commentSubmitting = ref(false)
const commentPage = ref(1)
const commentSize = ref(10)
const replyOpen = ref(false)
const replySubmitting = ref(false)
const replyDraft = ref('')
const replyError = ref('')
const replyTarget = ref<Comment | null>(null)
const deleteOpen = ref(false)
const deleteSubmitting = ref(false)
const deleteTarget = ref<Comment | null>(null)

const contentDelta = computed(() => {
  if (!blog.value?.content) return undefined
  try {
    return JSON.parse(blog.value.content)
  } catch {
    return undefined
  }
})

const createdAt = computed(() => {
  if (!blog.value?.createAt) return '—'
  const date = new Date(blog.value.createAt)
  if (Number.isNaN(date.getTime())) return blog.value.createAt
  return date.toLocaleDateString()
})

const likeText = computed(() => {
  if (!blog.value) return '点赞'
  return liked.value ? `已点赞 ${blog.value.likeCount ?? 0}` : `点赞 ${blog.value.likeCount ?? 0}`
})

const fetchDetail = async () => {
  loading.value = true
  try {
    blog.value = await getBlogDetail(blogId.value)
  } catch {
    blog.value = null
    window.dispatchEvent(
      new CustomEvent('toast', {
        detail: { title: '加载失败', message: '无法获取博客详情。', type: 'error' },
      }),
    )
  } finally {
    loading.value = false
  }
}

const fetchComments = async () => {
  commentsLoading.value = true
  try {
    const data = await listBlogComments(blogId.value, {
      page: commentPage.value - 1,
      size: commentSize.value,
    })
    comments.value = data.content ?? []
  } catch {
    comments.value = []
    window.dispatchEvent(
      new CustomEvent('toast', {
        detail: { title: '加载失败', message: '无法获取评论列表。', type: 'error' },
      }),
    )
  } finally {
    commentsLoading.value = false
  }
}

const submitComment = async () => {
  if (!authStore.isAuthenticated) {
    window.dispatchEvent(
      new CustomEvent('toast', {
        detail: { title: '请先登录', message: '登录后才能发表评论。', type: 'warning' },
      }),
    )
    return
  }
  commentError.value = commentDraft.value ? '' : '请输入评论内容'
  if (commentError.value) return
  commentSubmitting.value = true
  try {
    await createBlogComment(blogId.value, { content: commentDraft.value })
    commentDraft.value = ''
    window.dispatchEvent(
      new CustomEvent('toast', {
        detail: { title: '评论成功', message: '已发布评论。', type: 'success' },
      }),
    )
    await fetchComments()
  } catch {
    window.dispatchEvent(
      new CustomEvent('toast', {
        detail: { title: '评论失败', message: '请稍后再试。', type: 'error' },
      }),
    )
  } finally {
    commentSubmitting.value = false
  }
}

const openReply = (comment: Comment) => {
  if (!authStore.isAuthenticated) {
    window.dispatchEvent(
      new CustomEvent('toast', {
        detail: { title: '请先登录', message: '登录后才能回复评论。', type: 'warning' },
      }),
    )
    return
  }
  replyTarget.value = comment
  replyDraft.value = ''
  replyError.value = ''
  replyOpen.value = true
}

const submitReply = async () => {
  replyError.value = replyDraft.value ? '' : '请输入回复内容'
  if (replyError.value || !replyTarget.value?.commentId) return
  replySubmitting.value = true
  try {
    await replyComment(replyTarget.value.commentId, { content: replyDraft.value })
    replyDraft.value = ''
    replyOpen.value = false
    window.dispatchEvent(
      new CustomEvent('toast', {
        detail: { title: '回复成功', message: '已发布回复。', type: 'success' },
      }),
    )
    await fetchComments()
  } catch {
    window.dispatchEvent(
      new CustomEvent('toast', {
        detail: { title: '回复失败', message: '请稍后再试。', type: 'error' },
      }),
    )
  } finally {
    replySubmitting.value = false
  }
}

const openDelete = (comment: Comment) => {
  deleteTarget.value = comment
  deleteOpen.value = true
}

const confirmDelete = async () => {
  if (!deleteTarget.value?.commentId) return
  deleteSubmitting.value = true
  try {
    await deleteComment(deleteTarget.value.commentId)
    window.dispatchEvent(
      new CustomEvent('toast', {
        detail: { title: '删除成功', message: '评论已删除。', type: 'success' },
      }),
    )
    deleteOpen.value = false
    await fetchComments()
  } catch {
    window.dispatchEvent(
      new CustomEvent('toast', {
        detail: { title: '删除失败', message: '请稍后再试。', type: 'error' },
      }),
    )
  } finally {
    deleteSubmitting.value = false
  }
}

const canDelete = (comment: Comment) => {
  const username = authStore.user?.username
  if (!username) return false
  if (comment.user?.username === username) return true
  if (blog.value?.username === username) return true
  return authStore.isAdmin
}

const toggleCommentLike = async (comment: Comment) => {
  if (!authStore.isAuthenticated) {
    window.dispatchEvent(
      new CustomEvent('toast', {
        detail: { title: '请先登录', message: '登录后才能点赞。', type: 'warning' },
      }),
    )
    return
  }
  if (!comment.commentId) return
  const original = comment.likeCount ?? 0
  const nextLiked = !comment.__liked
  comment.__liked = nextLiked
  comment.likeCount = Math.max(0, original + (nextLiked ? 1 : -1))
  try {
    if (nextLiked) {
      const result = await likeComment(comment.commentId ?? 0)
      if (result?.like_count !== undefined) {
        comment.likeCount = result.like_count
      }
    } else {
      const result = await unlikeComment(comment.commentId ?? 0)
      if (result?.like_count !== undefined) {
        comment.likeCount = result.like_count
      }
    }
  } catch {
    comment.__liked = !nextLiked
    comment.likeCount = original
    window.dispatchEvent(
      new CustomEvent('toast', {
        detail: { title: '操作失败', message: '请稍后再试。', type: 'error' },
      }),
    )
  }
}

const toggleLike = async () => {
  if (!blog.value || liking.value) return
  liking.value = true
  const original = blog.value.likeCount ?? 0
  const nextLiked = !liked.value
  liked.value = nextLiked
  blog.value.likeCount = Math.max(0, original + (nextLiked ? 1 : -1))
  try {
    if (nextLiked) {
      const result = await likeBlog(blog.value.id ?? blogId.value)
      if (result?.like_count !== undefined) {
        blog.value.likeCount = result.like_count
      }
    } else {
      const result = await unlikeBlog(blog.value.id ?? blogId.value)
      if (result?.like_count !== undefined) {
        blog.value.likeCount = result.like_count
      }
    }
  } catch {
    liked.value = !nextLiked
    blog.value.likeCount = original
    window.dispatchEvent(
      new CustomEvent('toast', {
        detail: { title: '操作失败', message: '请稍后再试。', type: 'error' },
      }),
    )
  } finally {
    liking.value = false
  }
}

onMounted(() => {
  fetchDetail()
  fetchComments()
})
</script>

<style scoped>
.blog-detail {
  display: grid;
  gap: 24px;
}

.blog-detail__header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
}

.blog-detail__eyebrow {
  font-size: 12px;
  letter-spacing: 0.2em;
  text-transform: uppercase;
  color: var(--color-text-muted);
  margin-bottom: 10px;
}

.blog-detail__meta {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 12px;
  color: var(--color-text-muted);
  text-transform: uppercase;
  letter-spacing: 0.12em;
}

.dot {
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background: var(--color-primary);
}

.blog-detail__state {
  padding: 20px 0;
}

.blog-detail__content {
  display: grid;
  gap: 16px;
  padding: 24px;
  border-radius: var(--radius-lg);
  background: var(--color-surface);
  box-shadow: var(--shadow-sm);
}

.blog-detail__tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

@media (max-width: 900px) {
  .blog-detail__header {
    flex-direction: column;
  }
}
</style>
