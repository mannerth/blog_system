<template>
  <section class="my-blogs">
    <header class="my-blogs__header">
      <div>
        <p class="my-blogs__eyebrow">My Blogs</p>
        <h1>我的博客</h1>
        <p class="my-blogs__desc">管理你发布过的内容，支持编辑与删除。</p>
      </div>
      <BaseButton @click="goCreate">发布新博客</BaseButton>
    </header>

    <LoadingState v-if="loading" />
    <EmptyState
      v-else-if="!blogs.length"
      title="暂无博客"
      description="开始发布你的第一篇文章吧。"
    >
      <template #action>
        <BaseButton variant="outline" @click="goCreate">立即发布</BaseButton>
      </template>
    </EmptyState>
    <div v-else class="my-blogs__list">
      <article v-for="blog in blogs" :key="blog.id" class="my-blogs__card">
        <div>
          <h3 class="my-blogs__title">{{ blog.title || '未命名标题' }}</h3>
          <p class="my-blogs__meta">
            <span>{{ blog.categoryName || '未分类' }}</span>
            <span class="dot"></span>
            <span>{{ formatDate(blog.createAt) }}</span>
          </p>
        </div>
        <div class="my-blogs__actions">
          <BaseButton variant="outline" @click="goEdit(blog.id)">编辑</BaseButton>
          <BaseButton variant="ghost" @click="openDelete(blog)">删除</BaseButton>
        </div>
      </article>
    </div>

    <BasePagination
      v-if="total > 0"
      :total="total"
      :page="page"
      :size="size"
      @update:page="handlePageChange"
      @update:size="handleSizeChange"
    />

    <BaseModal v-model="deleteOpen" title="确认删除" eyebrow="Danger">
      <p>确定要删除这篇博客吗？此操作不可撤销。</p>
      <template #footer>
        <BaseButton variant="outline" @click="deleteOpen = false">取消</BaseButton>
        <BaseButton variant="ghost" :loading="deleting" @click="confirmDelete">确认删除</BaseButton>
      </template>
    </BaseModal>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import BaseButton from '@/components/base/BaseButton.vue'
import BaseModal from '@/components/base/BaseModal.vue'
import BasePagination from '@/components/base/BasePagination.vue'
import EmptyState from '@/components/base/EmptyState.vue'
import LoadingState from '@/components/base/LoadingState.vue'
import { deleteBlog, listMyBlogs, type Blog } from '@/api/blogs'

const router = useRouter()

const loading = ref(false)
const deleting = ref(false)
const blogs = ref<Blog[]>([])
const total = ref(0)
const page = ref(1)
const size = ref(10)

const deleteOpen = ref(false)
const deleteTarget = ref<Blog | null>(null)

const fetchBlogs = async () => {
  loading.value = true
  try {
    const data = await listMyBlogs({ page: page.value, size: size.value })
    blogs.value = data.content ?? []
    total.value = data.totalElements ?? 0
  } catch {
    blogs.value = []
    total.value = 0
    window.dispatchEvent(
      new CustomEvent('toast', {
        detail: { title: '加载失败', message: '无法获取我的博客。', type: 'error' },
      }),
    )
  } finally {
    loading.value = false
  }
}

const goCreate = () => router.push('/editor')
const goEdit = (id?: number) => {
  if (!id) return
  router.push(`/editor/${id}`)
}

const openDelete = (blog: Blog) => {
  deleteTarget.value = blog
  deleteOpen.value = true
}

const confirmDelete = async () => {
  if (!deleteTarget.value?.id) return
  deleting.value = true
  try {
    await deleteBlog(deleteTarget.value.id)
    window.dispatchEvent(
      new CustomEvent('toast', {
        detail: { title: '删除成功', message: '博客已删除。', type: 'success' },
      }),
    )
    deleteOpen.value = false
    await fetchBlogs()
  } catch {
    window.dispatchEvent(
      new CustomEvent('toast', {
        detail: { title: '删除失败', message: '请稍后再试。', type: 'error' },
      }),
    )
  } finally {
    deleting.value = false
  }
}

const handlePageChange = (nextPage: number) => {
  page.value = nextPage
  fetchBlogs()
}

const handleSizeChange = (nextSize: number) => {
  size.value = nextSize
  page.value = 1
  fetchBlogs()
}

const formatDate = (value?: string) => {
  if (!value) return '—'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value
  return date.toLocaleDateString()
}

onMounted(() => {
  fetchBlogs()
})
</script>

<style scoped>
.my-blogs {
  display: grid;
  gap: 24px;
}

.my-blogs__header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
}

.my-blogs__eyebrow {
  font-size: 12px;
  letter-spacing: 0.2em;
  text-transform: uppercase;
  color: var(--color-text-muted);
  margin-bottom: 10px;
}

.my-blogs__desc {
  color: var(--color-text-muted);
}

.my-blogs__list {
  display: grid;
  gap: 14px;
}

.my-blogs__card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 18px 20px;
  border-radius: var(--radius-lg);
  background: var(--color-surface);
  box-shadow: var(--shadow-sm);
}

.my-blogs__title {
  margin: 0 0 8px;
}

.my-blogs__meta {
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

.my-blogs__actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

@media (max-width: 900px) {
  .my-blogs__header {
    flex-direction: column;
  }

  .my-blogs__card {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
