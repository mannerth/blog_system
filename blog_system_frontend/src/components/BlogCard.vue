<template>
  <article class="blog-card">
    <div class="blog-card__header">
      <RouterLink v-if="detailPath" :to="detailPath" class="blog-card__title">
        {{ blog.title || '未命名标题' }}
      </RouterLink>
      <span v-else class="blog-card__title">{{ blog.title || '未命名标题' }}</span>
      <div class="blog-card__meta">
        <span>{{ authorName }}</span>
        <span class="dot"></span>
        <span>{{ categoryName }}</span>
        <span class="dot"></span>
        <span>{{ createdAt }}</span>
      </div>
    </div>

    <div v-if="tags.length" class="blog-card__tags">
      <BaseTag v-for="tag in tags" :key="tagKey(tag)" variant="outline">
        {{ tag }}
      </BaseTag>
    </div>

    <div class="blog-card__stats">
      <span>浏览 {{ blog.viewCount ?? 0 }}</span>
      <span>点赞 {{ blog.likeCount ?? 0 }}</span>
    </div>
  </article>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { RouterLink } from 'vue-router'
import BaseTag from '@/components/base/BaseTag.vue'
import type { Blog } from '@/api/blogs'

const props = defineProps<{
  blog: Blog
}>()

const detailPath = computed(() => (props.blog.id ? `/blogs/${props.blog.id}` : ''))
const authorName = computed(() => props.blog.username ?? '佚名')
const categoryName = computed(() => props.blog.categoryName ?? '未分类')
const tags = computed(() => props.blog.tagNames ?? [])

const createdAt = computed(() => {
  if (!props.blog.createAt) return '—'
  const date = new Date(props.blog.createAt)
  if (Number.isNaN(date.getTime())) return props.blog.createAt
  return date.toLocaleDateString()
})

const tagKey = (tag: string, index?: number) => `${props.blog.id ?? 'blog'}-${tag}-${index ?? 0}`
</script>

<style scoped>
.blog-card {
  padding: 20px;
  border-radius: var(--radius-lg);
  background: var(--color-surface);
  box-shadow: var(--shadow-sm);
  display: grid;
  gap: 14px;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.blog-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.blog-card__header {
  display: grid;
  gap: 8px;
}

.blog-card__title {
  font-size: 20px;
  font-weight: 700;
  color: var(--color-heading);
  text-decoration: none;
}

.blog-card__meta {
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

.blog-card__tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.blog-card__stats {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: var(--color-text-muted);
}
</style>
