<template>
  <section class="admin-categories">
    <header class="admin-categories__header">
      <div>
        <p class="admin-categories__eyebrow">Categories</p>
        <h1>分类管理</h1>
        <p class="admin-categories__desc">维护站点分类，博客可按分类进行归档。</p>
      </div>
      <BaseButton @click="openAdd">新增分类</BaseButton>
    </header>

    <LoadingState v-if="loading" />
    <EmptyState
      v-else-if="!categories.length"
      title="暂无分类"
      description="点击上方按钮创建第一个分类。"
    />

    <div v-else class="admin-categories__list">
      <article v-for="cat in categories" :key="cat.id" class="admin-categories__card">
        <div>
          <h3 class="admin-categories__name">{{ cat.name }}</h3>
          <p v-if="cat.description" class="admin-categories__desc-text">{{ cat.description }}</p>
          <p class="admin-categories__meta">
            <span>创建于 {{ formatDate(cat.createAt) }}</span>
          </p>
        </div>
        <div class="admin-categories__actions">
          <BaseButton variant="outline" @click="openEdit(cat)">编辑</BaseButton>
          <BaseButton variant="ghost" @click="openDelete(cat)">删除</BaseButton>
        </div>
      </article>
    </div>

    <BaseModal v-model="formOpen" :title="isEditing ? '编辑分类' : '新增分类'" eyebrow="Categories">
      <form @submit.prevent="handleSubmit">
        <BaseInput
          v-model="form.name"
          label="分类名称"
          placeholder="输入分类名称"
          :error="errors.name"
        />
        <BaseInput
          v-model="form.description"
          label="描述（可选）"
          placeholder="输入分类描述"
        />
      </form>
      <template #footer>
        <BaseButton variant="outline" @click="formOpen = false">取消</BaseButton>
        <BaseButton :loading="saving" @click="handleSubmit">
          {{ isEditing ? '保存' : '创建' }}
        </BaseButton>
      </template>
    </BaseModal>

    <BaseModal v-model="deleteOpen" title="确认删除" eyebrow="Danger">
      <p>确定要删除分类「{{ deleteTarget?.name }}」吗？该分类下的博客将变为未分类。</p>
      <template #footer>
        <BaseButton variant="outline" @click="deleteOpen = false">取消</BaseButton>
        <BaseButton variant="ghost" :loading="deleting" @click="confirmDelete">确认删除</BaseButton>
      </template>
    </BaseModal>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import BaseButton from '@/components/base/BaseButton.vue'
import BaseInput from '@/components/base/BaseInput.vue'
import BaseModal from '@/components/base/BaseModal.vue'
import EmptyState from '@/components/base/EmptyState.vue'
import LoadingState from '@/components/base/LoadingState.vue'
import {
  createCategory,
  deleteCategory,
  listCategories,
  updateCategory,
  type CategoryResponse,
} from '@/api/categories'

const loading = ref(false)
const saving = ref(false)
const deleting = ref(false)
const categories = ref<CategoryResponse[]>([])

const formOpen = ref(false)
const isEditing = ref(false)
const editingId = ref<number | null>(null)
const form = reactive({ name: '', description: '' })
const errors = reactive({ name: '' })

const deleteOpen = ref(false)
const deleteTarget = ref<CategoryResponse | null>(null)

const fetchCategories = async () => {
  loading.value = true
  try {
    categories.value = await listCategories()
  } catch {
    categories.value = []
    window.dispatchEvent(
      new CustomEvent('toast', {
        detail: { title: '加载失败', message: '无法获取分类列表。', type: 'error' },
      }),
    )
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  form.name = ''
  form.description = ''
  errors.name = ''
  isEditing.value = false
  editingId.value = null
}

const openAdd = () => {
  resetForm()
  formOpen.value = true
}

const openEdit = (cat: CategoryResponse) => {
  resetForm()
  isEditing.value = true
  editingId.value = cat.id
  form.name = cat.name
  form.description = cat.description ?? ''
  formOpen.value = true
}

const validate = () => {
  errors.name = form.name.trim() ? '' : '请输入分类名称'
  return !errors.name
}

const handleSubmit = async () => {
  if (!validate()) return
  saving.value = true
  try {
    if (isEditing.value && editingId.value !== null) {
      await updateCategory(editingId.value, {
        name: form.name.trim(),
        description: form.description.trim() || undefined,
      })
      window.dispatchEvent(
        new CustomEvent('toast', {
          detail: { title: '已更新', message: '分类信息已保存。', type: 'success' },
        }),
      )
    } else {
      await createCategory({
        name: form.name.trim(),
        description: form.description.trim() || undefined,
      })
      window.dispatchEvent(
        new CustomEvent('toast', {
          detail: { title: '创建成功', message: '新分类已添加。', type: 'success' },
        }),
      )
    }
    formOpen.value = false
    await fetchCategories()
  } catch (err: unknown) {
    const message =
      err instanceof Error ? err.message : '请稍后再试。'
    window.dispatchEvent(
      new CustomEvent('toast', {
        detail: { title: '操作失败', message, type: 'error' },
      }),
    )
  } finally {
    saving.value = false
  }
}

const openDelete = (cat: CategoryResponse) => {
  deleteTarget.value = cat
  deleteOpen.value = true
}

const confirmDelete = async () => {
  if (!deleteTarget.value?.id) return
  deleting.value = true
  try {
    await deleteCategory(deleteTarget.value.id)
    window.dispatchEvent(
      new CustomEvent('toast', {
        detail: { title: '删除成功', message: '分类已删除。', type: 'success' },
      }),
    )
    deleteOpen.value = false
    await fetchCategories()
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

const formatDate = (value?: string) => {
  if (!value) return '—'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value
  return date.toLocaleDateString()
}

onMounted(() => {
  fetchCategories()
})
</script>

<style scoped>
.admin-categories {
  display: grid;
  gap: 24px;
}

.admin-categories__header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
}

.admin-categories__eyebrow {
  font-size: 12px;
  letter-spacing: 0.2em;
  text-transform: uppercase;
  color: var(--color-text-muted);
  margin-bottom: 10px;
}

.admin-categories__desc {
  color: var(--color-text-muted);
}

.admin-categories__list {
  display: grid;
  gap: 14px;
}

.admin-categories__card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 18px 20px;
  border-radius: var(--radius-lg);
  background: var(--color-surface);
  box-shadow: var(--shadow-sm);
}

.admin-categories__name {
  margin: 0 0 6px;
}

.admin-categories__desc-text {
  margin: 0 0 6px;
  color: var(--color-text-muted);
}

.admin-categories__meta {
  font-size: 12px;
  color: var(--color-text-muted);
  text-transform: uppercase;
  letter-spacing: 0.12em;
}

.admin-categories__actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  flex-shrink: 0;
}

@media (max-width: 900px) {
  .admin-categories__header {
    flex-direction: column;
  }

  .admin-categories__card {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
