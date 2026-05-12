<template>
  <article class="comment-item" :style="indentStyle">
    <div class="comment-item__avatar">
      <img v-if="avatarUrl" :src="avatarUrl" :alt="authorName" />
      <span v-else>{{ authorInitial }}</span>
    </div>
    <div class="comment-item__body">
      <header class="comment-item__meta">
        <span class="comment-item__author">{{ authorName }}</span>
        <span class="dot"></span>
        <span class="comment-item__time">{{ createdAt }}</span>
      </header>
      <p class="comment-item__content">{{ comment.content || '—' }}</p>
      <footer class="comment-item__footer">
        <span class="comment-item__likes">点赞 {{ comment.likeCount ?? 0 }}</span>
        <div v-if="hasActions" class="comment-item__actions">
          <slot name="actions" :comment="comment" />
        </div>
      </footer>
    </div>
  </article>

  <div v-if="comment.replies?.length" class="comment-item__replies">
    <CommentItem
      v-for="reply in comment.replies"
      :key="reply.commentId"
      :comment="reply"
      :depth="depth + 1"
    >
      <template #actions="slotProps">
        <slot name="actions" v-bind="slotProps" />
      </template>
    </CommentItem>
  </div>
</template>

<script setup lang="ts">
import { computed, useSlots } from 'vue'
import type { Comment } from '@/api/comments'

defineOptions({ name: 'CommentItem' })

const props = withDefaults(
  defineProps<{
    comment: Comment
    depth?: number
  }>(),
  {
    depth: 0,
  },
)

const slots = useSlots()
const hasActions = computed(() => Boolean(slots.actions))

const authorName = computed(() => props.comment.user?.username ?? '匿名用户')
const authorInitial = computed(() => authorName.value.slice(0, 1))
const avatarUrl = computed(() => props.comment.user?.avatarUrl ?? '')

const createdAt = computed(() => {
  if (!props.comment.createdAt) return '—'
  const date = new Date(props.comment.createdAt)
  if (Number.isNaN(date.getTime())) return props.comment.createdAt
  return date.toLocaleString()
})

const indentStyle = computed(() => ({ '--level': String(props.depth) }))
</script>

<style scoped>
.comment-item {
  display: grid;
  grid-template-columns: 44px 1fr;
  gap: 12px;
  padding: 16px;
  border-radius: var(--radius-md);
  background: var(--color-surface);
  box-shadow: var(--shadow-sm);
  margin-bottom: 14px;
  padding-left: calc(16px + var(--level) * 20px);
}

.comment-item__avatar {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: var(--color-primary-soft);
  color: var(--color-primary-strong);
  display: grid;
  place-items: center;
  font-weight: 700;
  overflow: hidden;
}

.comment-item__avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.comment-item__meta {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 12px;
  color: var(--color-text-muted);
  text-transform: uppercase;
  letter-spacing: 0.12em;
}

.comment-item__author {
  color: var(--color-heading);
  font-weight: 600;
}

.dot {
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background: var(--color-primary);
}

.comment-item__content {
  margin: 10px 0 0;
  color: var(--color-text);
}

.comment-item__footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
  font-size: 12px;
  color: var(--color-text-muted);
}

.comment-item__actions {
  display: flex;
  gap: 8px;
}

.comment-item__replies {
  margin-left: 20px;
  padding-left: 12px;
  border-left: 1px dashed var(--color-border);
}

@media (max-width: 640px) {
  .comment-item {
    grid-template-columns: 36px 1fr;
    padding: 12px;
  }

  .comment-item__avatar {
    width: 36px;
    height: 36px;
  }
}
</style>
