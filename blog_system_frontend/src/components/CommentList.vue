<template>
  <section class="comment-list">
    <header class="comment-list__header">
      <div>
        <p class="comment-list__eyebrow">Comments</p>
        <h2>评论区</h2>
      </div>
      <BaseButton variant="outline" @click="emitRefresh">刷新</BaseButton>
    </header>

    <div class="comment-list__composer">
      <BaseTextarea v-model="draft" placeholder="写下你的评论..." :error="error" clearable />
      <div class="comment-list__actions">
        <BaseButton :loading="submitting" @click="emitSubmit">发表评论</BaseButton>
      </div>
    </div>

    <LoadingState v-if="loading" />
    <EmptyState v-else-if="!comments.length" title="暂无评论" description="抢先发表第一条评论吧。" />
    <div v-else class="comment-list__items">
      <CommentItem v-for="comment in comments" :key="comment.commentId" :comment="comment">
        <template #actions="slotProps">
          <slot name="actions" v-bind="slotProps" />
        </template>
      </CommentItem>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import BaseButton from '@/components/base/BaseButton.vue'
import BaseTextarea from '@/components/base/BaseTextarea.vue'
import EmptyState from '@/components/base/EmptyState.vue'
import LoadingState from '@/components/base/LoadingState.vue'
import CommentItem from '@/components/CommentItem.vue'
import type { Comment } from '@/api/comments'

const props = withDefaults(
  defineProps<{
    comments: Comment[]
    loading?: boolean
    modelValue?: string
    error?: string
    submitting?: boolean
  }>(),
  {
    comments: () => [],
    loading: false,
    modelValue: '',
    error: '',
    submitting: false,
  },
)

const emit = defineEmits<{
  (event: 'update:modelValue', value: string): void
  (event: 'submit'): void
  (event: 'refresh'): void
}>()

const draft = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value),
})

const emitSubmit = () => emit('submit')
const emitRefresh = () => emit('refresh')
</script>

<style scoped>
.comment-list {
  display: grid;
  gap: 18px;
}

.comment-list__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.comment-list__eyebrow {
  font-size: 12px;
  letter-spacing: 0.2em;
  text-transform: uppercase;
  color: var(--color-text-muted);
  margin-bottom: 10px;
}

.comment-list__composer {
  padding: 18px;
  border-radius: var(--radius-lg);
  background: var(--color-surface);
  box-shadow: var(--shadow-sm);
  display: grid;
  gap: 12px;
}

.comment-list__actions {
  display: flex;
  justify-content: flex-end;
}

.comment-list__items {
  display: grid;
  gap: 12px;
}

@media (max-width: 640px) {
  .comment-list__header {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
