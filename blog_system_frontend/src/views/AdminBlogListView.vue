<template>
  <section class="admin-blogs">
    <header class="admin-blogs__header">
      <div>
        <p class="admin-blogs__eyebrow">Blogs</p>
        <h1>博客管理</h1>
        <p class="admin-blogs__desc">查看、筛选、编辑与删除全站博客。</p>
      </div>
    </header>

    <div class="admin-blogs__filters">
      <BaseInput v-model="keyword" placeholder="搜索标题或内容" clearable @keydown.enter="handleSearch" />
      <BaseSelect
        v-model="filters.category"
        label="分类"
        placeholder="全部分类"
        :options="categoryOptions"
      />
      <BaseSelect v-model="filters.sort" :options="sortOptions" placeholder="排序" />
      <BaseButton variant="ghost" @click="resetFilters">重置</BaseButton>
    </div>

    <TagChipSelector
      v-model="filters.tagIds"
      label="标签"
      :options="tagChipOptions"
    />

    <LoadingState v-if="loading" />
    <EmptyState v-else-if="!blogs.length" title="暂无博客" description="全站还没有任何人发布博客。" />

    <div v-else class="admin-blogs__list">
      <article v-for="blog in blogs" :key="blog.id" class="admin-blogs__card">
        <div>
          <h3 class="admin-blogs__title">{{ blog.title || '未命名标题' }}</h3>
          <p class="admin-blogs__meta">
            <span>{{ blog.username || '佚名' }}</span>
            <span class="dot"></span>
            <span>{{ blog.categoryName || '未分类' }}</span>
            <span class="dot"></span>
            <span>{{ formatDate(blog.createAt) }}</span>
          </p>
        </div>
        <div class="admin-blogs__actions">
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
      <p>确定要删除博客「{{ deleteTarget?.title }}」吗？此操作不可撤销。</p>
      <template #footer>
        <BaseButton variant="outline" @click="deleteOpen = false">取消</BaseButton>
        <BaseButton variant="ghost" :loading="deleting" @click="confirmDelete">确认删除</BaseButton>
      </template>
    </BaseModal>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import BaseButton from '@/components/base/BaseButton.vue'
import BaseInput from '@/components/base/BaseInput.vue'
import BaseSelect, { type SelectOption } from '@/components/base/BaseSelect.vue'
import BaseModal from '@/components/base/BaseModal.vue'
import BasePagination from '@/components/base/BasePagination.vue'
import EmptyState from '@/components/base/EmptyState.vue'
import LoadingState from '@/components/base/LoadingState.vue'
import TagChipSelector from '@/components/base/TagChipSelector.vue'
import { deleteAdminBlog, listAdminBlogs, type Blog } from '@/api/blogs'
import { listCategories } from '@/api/categories'
import { listTags } from '@/api/tags'

const router = useRouter()

const loading = ref(false)
const deleting = ref(false)
const blogs = ref<Blog[]>([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const keyword = ref('')

const filters = reactive({
  category: '',
  tagIds: [] as number[],
  sort: 'createAt,desc',
})

const sortOptions: SelectOption[] = [
  { label: '最新发布', value: 'createAt,desc' },
  { label: '最早发布', value: 'createAt,asc' },
  { label: '最多浏览', value: 'viewCount,desc' },
  { label: '最多点赞', value: 'likeCount,desc' },
]

const categoryOptions = ref<SelectOption[]>([])
const tagChipOptions = ref<SelectOption[]>([])

const deleteOpen = ref(false)
const deleteTarget = ref<Blog | null>(null)

const fetchBlogs = async () => {
  loading.value = true
  try {
    const data = await listAdminBlogs({
      page: page.value,
      size: size.value,
      category_id: filters.category || undefined,
      tagIds: filters.tagIds.length > 0 ? filters.tagIds : undefined,
      keyword: keyword.value || undefined,
      sort: filters.sort || undefined,
    })
    blogs.value = data.content ?? []
    total.value = data.totalElements ?? 0
  } catch {
    blogs.value = []
    total.value = 0
    window.dispatchEvent(
      new CustomEvent('toast', {
        detail: { title: '加载失败', message: '无法获取博客列表。', type: 'error' },
      }),
    )
  } finally {
    loading.value = false
  }
}

const fetchFilters = async () => {
  try {
    const [categories, tags] = await Promise.all([listCategories(), listTags()])
    categoryOptions.value = [
      { label: '全部分类', value: '' },
      ...categories.map((cat) => ({
        label: cat.name ?? '未命名',
        value: String(cat.id ?? ''),
      })),
    ]
    tagChipOptions.value = tags.map((tag) => ({
      label: tag.name ?? '标签',
      value: tag.id ?? 0,
    }))
  } catch {
    categoryOptions.value = [{ label: '全部分类', value: '' }]
    tagChipOptions.value = []
  }
}

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
    await deleteAdminBlog(deleteTarget.value.id)
    window.dispatchEvent(
      new CustomEvent('toast', {
        detail: { title: '删除成功', message: '博客已删除。', type: 'success' },
      }),
    )
    deleteOpen.value = false
    if (blogs.value.length === 1 && page.value > 1) {
      page.value -= 1
    }
    await fetchBlogs()
  } catch (err: unknown) {
    const message =
      err instanceof Error ? err.message : '请稍后再试。'
    window.dispatchEvent(
      new CustomEvent('toast', {
        detail: { title: '删除失败', message, type: 'error' },
      }),
    )
  } finally {
    deleting.value = false
  }
}

const handlePageChange = (nextPage: number) => {
  page.value = nextPage
}

const handleSizeChange = (nextSize: number) => {
  size.value = nextSize
  page.value = 1
}

const handleSearch = () => {
  page.value = 1
  fetchBlogs()
}

const resetFilters = () => {
  keyword.value = ''
  filters.category = ''
  filters.tagIds = []
  filters.sort = 'createAt,desc'
  page.value = 1
  fetchBlogs()
}

const formatDate = (value?: string) => {
  if (!value) return '—'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value
  return date.toLocaleDateString()
}

watch([page, size], () => {
  fetchBlogs()
})

watch(
  () => ({ ...filters }),
  () => {
    page.value = 1
    fetchBlogs()
  },
)

onMounted(() => {
  fetchFilters()
  fetchBlogs()
})
</script>

<style scoped>
.admin-blogs {
  display: grid;
  gap: 24px;
}

.admin-blogs__header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
}

.admin-blogs__eyebrow {
  font-size: 12px;
  letter-spacing: 0.2em;
  text-transform: uppercase;
  color: var(--color-text-muted);
  margin-bottom: 10px;
}

.admin-blogs__desc {
  color: var(--color-text-muted);
}

.admin-blogs__filters {
  display: grid;
  grid-template-columns: 1fr repeat(3, auto) auto;
  gap: 12px;
  align-items: end;
}

.admin-blogs__list {
  display: grid;
  gap: 14px;
}

.admin-blogs__card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 18px 20px;
  border-radius: var(--radius-lg);
  background: var(--color-surface);
  box-shadow: var(--shadow-sm);
}

.admin-blogs__title {
  margin: 0 0 8px;
}

.admin-blogs__meta {
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

.admin-blogs__actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  flex-shrink: 0;
}

@media (max-width: 1024px) {
  .admin-blogs__filters {
    grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  }
}

@media (max-width: 900px) {
  .admin-blogs__header {
    flex-direction: column;
  }

  .admin-blogs__card {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
