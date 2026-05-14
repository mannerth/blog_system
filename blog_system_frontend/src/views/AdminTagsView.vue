<template>
  <section class="admin-tags">
    <header class="admin-tags__header">
      <div>
        <p class="admin-tags__eyebrow">Tags</p>
        <h1>标签管理</h1>
        <p class="admin-tags__desc">管理全站标签，标签可附加到博客以便灵活检索。</p>
      </div>
      <BaseButton @click="openAdd">新增标签</BaseButton>
    </header>

    <LoadingState v-if="loading" />
    <EmptyState
      v-else-if="!tags.length"
      title="暂无标签"
      description="点击上方按钮创建第一个标签。"
    />

    <div v-else class="admin-tags__list">
      <article v-for="tag in tags" :key="tag.id" class="admin-tags__card">
        <div>
          <h3 class="admin-tags__name">{{ tag.name }}</h3>
          <p class="admin-tags__meta">
            <span>创建于 {{ formatDate(tag.createAt) }}</span>
          </p>
        </div>
        <div class="admin-tags__actions">
          <BaseButton variant="outline" @click="openEdit(tag)">编辑</BaseButton>
          <BaseButton variant="ghost" @click="openDelete(tag)">删除</BaseButton>
        </div>
      </article>
    </div>

    <BaseModal v-model="formOpen" :title="isEditing ? '编辑标签' : '新增标签'" eyebrow="Tags">
      <form @submit.prevent="handleSubmit">
        <BaseInput
          v-model="form.name"
          label="标签名称"
          placeholder="输入标签名称"
          :error="errors.name"
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
      <p>确定要删除标签「{{ deleteTarget?.name }}」吗？相关博客将解除该标签关联。</p>
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
  createTag,
  deleteTag,
  listTags,
  updateTag,
  type Tag,
} from '@/api/tags'

const loading = ref(false)
const saving = ref(false)
const deleting = ref(false)
const tags = ref<Tag[]>([])

const formOpen = ref(false)
const isEditing = ref(false)
const editingId = ref<number | null>(null)
const form = reactive({ name: '' })
const errors = reactive({ name: '' })

const deleteOpen = ref(false)
const deleteTarget = ref<Tag | null>(null)

const fetchTags = async () => {
  loading.value = true
  try {
    tags.value = await listTags()
  } catch {
    tags.value = []
    window.dispatchEvent(
      new CustomEvent('toast', {
        detail: { title: '加载失败', message: '无法获取标签列表。', type: 'error' },
      }),
    )
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  form.name = ''
  errors.name = ''
  isEditing.value = false
  editingId.value = null
}

const openAdd = () => {
  resetForm()
  formOpen.value = true
}

const openEdit = (tag: Tag) => {
  resetForm()
  isEditing.value = true
  editingId.value = tag.id
  form.name = tag.name
  formOpen.value = true
}

const validate = () => {
  errors.name = form.name.trim() ? '' : '请输入标签名称'
  return !errors.name
}

const handleSubmit = async () => {
  if (!validate()) return
  saving.value = true
  try {
    if (isEditing.value && editingId.value !== null) {
      await updateTag(editingId.value, { id: editingId.value, name: form.name.trim() })
      window.dispatchEvent(
        new CustomEvent('toast', {
          detail: { title: '已更新', message: '标签名称已保存。', type: 'success' },
        }),
      )
    } else {
      await createTag({ name: form.name.trim() })
      window.dispatchEvent(
        new CustomEvent('toast', {
          detail: { title: '创建成功', message: '新标签已添加。', type: 'success' },
        }),
      )
    }
    formOpen.value = false
    await fetchTags()
  } catch {
    window.dispatchEvent(
      new CustomEvent('toast', {
        detail: { title: '操作失败', message: '请稍后再试。', type: 'error' },
      }),
    )
  } finally {
    saving.value = false
  }
}

const openDelete = (tag: Tag) => {
  deleteTarget.value = tag
  deleteOpen.value = true
}

const confirmDelete = async () => {
  if (!deleteTarget.value?.id) return
  deleting.value = true
  try {
    await deleteTag(deleteTarget.value.id)
    window.dispatchEvent(
      new CustomEvent('toast', {
        detail: { title: '删除成功', message: '标签已删除。', type: 'success' },
      }),
    )
    deleteOpen.value = false
    await fetchTags()
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
  fetchTags()
})
</script>

<style scoped>
.admin-tags {
  display: grid;
  gap: 24px;
}

.admin-tags__header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
}

.admin-tags__eyebrow {
  font-size: 12px;
  letter-spacing: 0.2em;
  text-transform: uppercase;
  color: var(--color-text-muted);
  margin-bottom: 10px;
}

.admin-tags__desc {
  color: var(--color-text-muted);
}

.admin-tags__list {
  display: grid;
  gap: 14px;
}

.admin-tags__card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 18px 20px;
  border-radius: var(--radius-lg);
  background: var(--color-surface);
  box-shadow: var(--shadow-sm);
}

.admin-tags__name {
  margin: 0 0 6px;
}

.admin-tags__meta {
  font-size: 12px;
  color: var(--color-text-muted);
  text-transform: uppercase;
  letter-spacing: 0.12em;
}

.admin-tags__actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  flex-shrink: 0;
}

@media (max-width: 900px) {
  .admin-tags__header {
    flex-direction: column;
  }

  .admin-tags__card {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
