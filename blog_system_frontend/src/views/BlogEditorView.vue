<template>
  <section class="editor">
    <header class="editor__header">
      <div>
        <p class="editor__eyebrow">Editor</p>
        <h1>{{ isEdit ? '编辑博客' : '发布新博客' }}</h1>
        <p class="editor__desc">支持自动保存草稿，避免内容丢失。</p>
      </div>
      <div class="editor__actions">
        <BaseButton variant="outline" @click="restoreDraft" :disabled="!hasDraft">
          恢复草稿
        </BaseButton>
        <BaseButton :loading="saving" type="submit" form="editor-form">
          {{ saving ? '提交中' : '发布' }}
        </BaseButton>
      </div>
    </header>

    <form id="editor-form" class="editor__form" @submit.prevent="handleSubmit">
      <BaseInput v-model="form.title" label="标题" placeholder="输入博客标题" :error="errors.title" />
      <div class="editor__grid">
        <BaseSelect
          v-model="form.category"
          label="分类"
          placeholder="选择分类"
          :options="categoryOptions"
          :error="errors.category"
        />
        <div class="editor__tags">
          <span class="editor__label">标签</span>
          <div class="editor__tag-list">
            <button
              v-for="tag in selectableTags"
              :key="tag.value"
              type="button"
              class="editor__tag"
              :class="{ 'is-active': isTagSelected(tag.value) }"
              @click="toggleTag(tag.value)"
            >
              {{ tag.label }}
            </button>
          </div>
          <p class="editor__hint">可多选，最多选择多个标签。</p>
        </div>
      </div>
      <MyQuillEditor v-model:content="form.content" />
    </form>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Delta } from 'quill'
import BaseButton from '@/components/base/BaseButton.vue'
import BaseInput from '@/components/base/BaseInput.vue'
import BaseSelect, { type SelectOption } from '@/components/base/BaseSelect.vue'
import MyQuillEditor from '@/components/MyQuillEditor.vue'
import { createBlog, getBlogDetail, updateBlog } from '@/api/blogs'
import { listCategories } from '@/api/categories'
import { listTags } from '@/api/tags'

const router = useRouter()
const route = useRoute()

const isEdit = computed(() => Boolean(route.params.id))
const saving = ref(false)
const form = reactive({
  title: '',
  category: '',
  tags: [] as string[],
  content: new Delta(),
})

const errors = reactive({
  title: '',
  category: '',
})

const categoryOptions = ref<SelectOption[]>([])
const tagOptions = ref<SelectOption[]>([])
const detailTagNames = ref<string[]>([])

const selectableTags = computed(() =>
  tagOptions.value.filter((tag) => tag.value !== '')
)

const draftKey = computed(() => `blog-editor-draft-${route.params.id || 'new'}`)
const hasDraft = ref(false)
const draftTimer = ref<number | null>(null)

const fetchFilters = async () => {
  try {
    const [categories, tags] = await Promise.all([listCategories(), listTags()])
    categoryOptions.value = [
      { label: '请选择分类', value: '' },
      ...categories.map((category) => ({
        label: category.name ?? '未命名',
        value: String(category.id ?? ''),
      })),
    ]
    tagOptions.value = [
      { label: '请选择标签', value: '' },
      ...tags.map((tag) => ({
        label: tag.name ?? '标签',
        value: String(tag.id ?? ''),
      })),
    ]
    applyDetailTags()
  } catch {
    categoryOptions.value = [{ label: '请选择分类', value: '' }]
    tagOptions.value = [{ label: '请选择标签', value: '' }]
  }
}

const validate = () => {
  errors.title = form.title ? '' : '请输入标题'
  errors.category = form.category ? '' : '请选择分类'
  return !errors.title && !errors.category
}

const handleSubmit = async () => {
  if (!validate()) return
  saving.value = true
  const payload = {
    title: form.title,
    content: JSON.stringify(form.content),
    category_id: Number(form.category),
    tags: form.tags,
  }
  try {
    if (isEdit.value) {
      const data = await updateBlog(Number(route.params.id), payload)
      window.dispatchEvent(
        new CustomEvent('toast', {
          detail: { title: '已更新', message: '博客内容已保存。', type: 'success' },
        }),
      )
      localStorage.removeItem(draftKey.value)
      await router.replace(`/blogs/${data.id}`)
    } else {
      const data = await createBlog(payload)
      window.dispatchEvent(
        new CustomEvent('toast', {
          detail: { title: '发布成功', message: '你的博客已发布。', type: 'success' },
        }),
      )
      localStorage.removeItem(draftKey.value)
      await router.replace(`/blogs/${data.id}`)
    }
  } catch {
    window.dispatchEvent(
      new CustomEvent('toast', {
        detail: { title: '提交失败', message: '请稍后再试。', type: 'error' },
      }),
    )
  } finally {
    saving.value = false
  }
}

const loadDetail = async () => {
  if (!isEdit.value) return
  try {
    const detail = await getBlogDetail(Number(route.params.id))
    form.title = detail.title ?? ''
    form.category = String(detail.categoryId ?? '')
    detailTagNames.value = detail.tagNames ?? []
    applyDetailTags()
    if (detail.content) {
      try {
        form.content = new Delta(JSON.parse(detail.content))
      } catch {
        form.content = new Delta()
      }
    }
  } catch {
    window.dispatchEvent(
      new CustomEvent('toast', {
        detail: { title: '加载失败', message: '无法加载博客详情。', type: 'error' },
      }),
    )
  }
}

const saveDraft = () => {
  const draft = {
    title: form.title,
    category: form.category,
    tags: form.tags,
    content: form.content,
  }
  localStorage.setItem(draftKey.value, JSON.stringify(draft))
  hasDraft.value = true
}

const restoreDraft = () => {
  const raw = localStorage.getItem(draftKey.value)
  if (!raw) return
  try {
    const draft = JSON.parse(raw) as {
      title: string
      category: string
      tags: string[] | string
      content: Delta
    }
    form.title = draft.title
    form.category = draft.category
    const rawTags = Array.isArray(draft.tags)
      ? draft.tags
      : draft.tags
        ? draft.tags.split(',').map((tag) => tag.trim()).filter(Boolean)
        : []
    form.tags = rawTags
    form.content = new Delta(draft.content)
  } catch {
    window.dispatchEvent(
      new CustomEvent('toast', {
        detail: { title: '草稿无效', message: '无法恢复草稿内容。', type: 'warning' },
      }),
    )
  }
}

const checkDraft = () => {
  hasDraft.value = Boolean(localStorage.getItem(draftKey.value))
}

const isTagSelected = (value: string | number) => form.tags.includes(String(value))

const toggleTag = (value: string | number) => {
  const id = String(value)
  if (form.tags.includes(id)) {
    form.tags = form.tags.filter((tag) => tag !== id)
  } else {
    form.tags = [...form.tags, id]
  }
}

const applyDetailTags = () => {
  if (!detailTagNames.value.length || !tagOptions.value.length) return
  const map = new Map(tagOptions.value.map((tag) => [tag.label, String(tag.value)]))
  const mapped = detailTagNames.value
    .map((name) => map.get(name))
    .filter((value): value is string => Boolean(value))
  if (mapped.length) {
    form.tags = mapped
  }
}

watch(
  () => ({ ...form }),
  () => {
    if (draftTimer.value) {
      window.clearTimeout(draftTimer.value)
    }
    draftTimer.value = window.setTimeout(() => {
      saveDraft()
    }, 400)
  },
  { deep: true },
)

onMounted(() => {
  fetchFilters()
  loadDetail()
  checkDraft()
})
</script>

<style scoped>
.editor {
  display: grid;
  gap: 24px;
}

.editor__header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
}

.editor__eyebrow {
  font-size: 12px;
  letter-spacing: 0.2em;
  text-transform: uppercase;
  color: var(--color-text-muted);
  margin-bottom: 10px;
}

.editor__desc {
  color: var(--color-text-muted);
}

.editor__actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.editor__form {
  display: grid;
  gap: 18px;
  padding: 24px;
  border-radius: var(--radius-lg);
  background: var(--color-surface);
  box-shadow: var(--shadow-sm);
}

.editor__grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 12px;
}

.editor__tags {
  display: grid;
  gap: 8px;
  padding: 14px;
  border-radius: var(--radius-md);
  border: 1px solid var(--color-border);
  background: var(--color-surface);
}

.editor__label {
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.12em;
  color: var(--color-text-muted);
}

.editor__tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.editor__tag {
  padding: 6px 12px;
  border-radius: 999px;
  border: 1px solid rgba(28, 155, 138, 0.3);
  background: transparent;
  color: var(--color-primary-strong);
  font-size: 12px;
  cursor: pointer;
  transition: background 0.2s ease, color 0.2s ease;
}

.editor__tag.is-active {
  background: var(--color-primary-soft);
  color: var(--color-primary-strong);
}

.editor__hint {
  margin: 0;
  font-size: 12px;
  color: var(--color-text-muted);
}

@media (max-width: 900px) {
  .editor__header {
    flex-direction: column;
  }
}
</style>
