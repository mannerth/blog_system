<template>
  <section class="home">
    <header class="home__header">
      <div>
        <p class="home__eyebrow">Explore</p>
        <h1>博客与灵感清单</h1>
        <p class="home__desc">浏览最新文章，按分类、标签或关键词筛选。</p>
      </div>
      <div class="home__controls">
        <BaseInput v-model="filters.keyword" placeholder="搜索标题或内容" clearable />
        <BaseSelect v-model="filters.sort" :options="sortOptions" placeholder="排序" />
      </div>
    </header>

    <div class="home__filters">
      <BaseSelect
        v-model="filters.category"
        label="分类"
        placeholder="全部分类"
        :options="categoryOptions"
      />
      <BaseSelect
        v-model="filters.tag"
        label="标签"
        placeholder="全部标签"
        :options="tagOptions"
      />
      <BaseButton variant="ghost" @click="resetFilters">重置筛选</BaseButton>
    </div>

    <LoadingState v-if="loading" />
    <EmptyState v-else-if="!blogs.length" title="暂无博客" description="换个关键词或清除筛选试试。">
      <template #action>
        <BaseButton variant="outline" @click="resetFilters">清除筛选</BaseButton>
      </template>
    </EmptyState>
    <div v-else class="home__grid">
      <BlogCard v-for="blog in blogs" :key="blog.id ?? blog.title" :blog="blog" />
    </div>

    <BasePagination
      v-if="total > 0"
      :total="total"
      :page="page"
      :size="size"
      @update:page="handlePageChange"
      @update:size="handleSizeChange"
    />
  </section>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import BlogCard from '@/components/BlogCard.vue'
import BaseButton from '@/components/base/BaseButton.vue'
import BaseInput from '@/components/base/BaseInput.vue'
import BaseSelect, { type SelectOption } from '@/components/base/BaseSelect.vue'
import BasePagination from '@/components/base/BasePagination.vue'
import EmptyState from '@/components/base/EmptyState.vue'
import LoadingState from '@/components/base/LoadingState.vue'
import { listBlogs } from '@/api/blogs'
import { listCategories } from '@/api/categories'
import { listTags } from '@/api/tags'

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const blogs = ref<Awaited<ReturnType<typeof listBlogs>>['items']>([])
const total = ref(0)
const page = ref(1)
const size = ref(10)

const filters = reactive({
  category: '',
  tag: '',
  keyword: '',
  sort: 'createAt,desc',
})

const sortOptions: SelectOption[] = [
  { label: '最新发布', value: 'createAt,desc' },
  { label: '最早发布', value: 'createAt,asc' },
  { label: '最多浏览', value: 'viewCount,desc' },
  { label: '最多点赞', value: 'likeCount,desc' },
]

const categoryOptions = ref<SelectOption[]>([])
const tagOptions = ref<SelectOption[]>([])
const queryKey = ref('')
const keywordTimer = ref<number | null>(null)

const syncFromQuery = () => {
  const query = route.query
  const nextPage = Math.max(1, Number(query.page ?? 1))
  const nextSize = Math.max(1, Number(query.size ?? 10))
  page.value = Number.isFinite(nextPage) ? nextPage : 1
  size.value = Number.isFinite(nextSize) ? nextSize : 10
  filters.category = typeof query.category_id === 'string' ? query.category_id : ''
  filters.tag = typeof query.tag === 'string' ? query.tag : ''
  filters.keyword = typeof query.keyword === 'string' ? query.keyword : ''
  const rawSort = typeof query.sort === 'string' ? query.sort : ''
  if (rawSort === '-created_at') {
    filters.sort = 'createAt,desc'
  } else if (rawSort === 'created_at') {
    filters.sort = 'createAt,asc'
  } else if (rawSort === '-view_count') {
    filters.sort = 'viewCount,desc'
  } else if (rawSort === '-like_count') {
    filters.sort = 'likeCount,desc'
  } else {
    filters.sort = rawSort || 'createAt,desc'
  }
}

const syncToQuery = () => {
  const nextQuery = {
    page: page.value,
    size: size.value,
    category_id: filters.category || undefined,
    tag: filters.tag || undefined,
    keyword: filters.keyword || undefined,
    sort: filters.sort || undefined,
  }
  const nextKey = JSON.stringify(nextQuery)
  if (nextKey === queryKey.value) return
  queryKey.value = nextKey
  router.replace({ query: nextQuery })
}

const fetchBlogs = async () => {
  loading.value = true
  try {
    const data = await listBlogs({
      page: page.value,
      size: size.value,
      category_id: filters.category || undefined,
      tag: filters.tag || undefined,
      keyword: filters.keyword,
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
      ...categories.map((category) => ({
        label: category.name ?? '未命名',
        value: String(category.id ?? ''),
      })),
    ]
    tagOptions.value = [
      { label: '全部标签', value: '' },
      ...tags.map((tag) => ({
        label: tag.name ?? '标签',
        value: String(tag.id ?? ''),
      })),
    ]
  } catch {
    categoryOptions.value = [{ label: '全部分类', value: '' }]
    tagOptions.value = [{ label: '全部标签', value: '' }]
  }
}

const handlePageChange = (nextPage: number) => {
  page.value = nextPage
}

const handleSizeChange = (nextSize: number) => {
  size.value = nextSize
  page.value = 1
}

const resetFilters = () => {
  filters.category = ''
  filters.tag = ''
  filters.keyword = ''
  filters.sort = 'createAt,desc'
  page.value = 1
}

watch(
  () => ({ ...filters, page: page.value, size: size.value }),
  (next, prev) => {
    if (next.keyword !== prev.keyword && keywordTimer.value) {
      window.clearTimeout(keywordTimer.value)
    }
    if (next.keyword !== prev.keyword) {
      keywordTimer.value = window.setTimeout(() => {
        page.value = 1
        syncToQuery()
      }, 350)
      return
    }
    if (next.category !== prev.category || next.tag !== prev.tag || next.sort !== prev.sort) {
      page.value = 1
    }
    syncToQuery()
  },
)

watch(
  () => route.query,
  () => {
    syncFromQuery()
  const currentKey = JSON.stringify({
      page: page.value,
      size: size.value,
      category_id: filters.category || undefined,
      tag: filters.tag || undefined,
      keyword: filters.keyword || undefined,
      sort: filters.sort || undefined,
    })
    if (currentKey === queryKey.value) return
    queryKey.value = currentKey
    fetchBlogs()
  },
  { immediate: true },
)

onMounted(() => {
  fetchFilters()
})

onUnmounted(() => {
  if (keywordTimer.value) {
    window.clearTimeout(keywordTimer.value)
  }
})
</script>

<style scoped>
.home {
  display: grid;
  gap: 24px;
}

.home__header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 24px;
}

.home__eyebrow {
  font-size: 12px;
  letter-spacing: 0.2em;
  text-transform: uppercase;
  color: var(--color-text-muted);
  margin-bottom: 12px;
}

.home__desc {
  color: var(--color-text-muted);
  max-width: 420px;
}

.home__controls {
  display: grid;
  gap: 12px;
  width: min(360px, 100%);
}

.home__filters {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 12px;
  align-items: end;
}

.home__grid {
  display: grid;
  gap: 18px;
}

@media (max-width: 900px) {
  .home__header {
    flex-direction: column;
    align-items: flex-start;
  }

  .home__controls {
    width: 100%;
  }
}
</style>
