<template>
  <section class="admin-users">
    <header class="admin-users__header">
      <div>
        <p class="admin-users__eyebrow">Users</p>
        <h1>用户管理</h1>
        <p class="admin-users__desc">查看、新增、编辑与删除用户账号。</p>
      </div>
      <BaseButton @click="openAdd">新增用户</BaseButton>
    </header>

    <LoadingState v-if="loading" />
    <EmptyState
      v-else-if="!users.length"
      title="暂无用户"
      description="点击上方按钮创建第一个用户。"
    />

    <div v-else class="admin-users__list">
      <article v-for="user in users" :key="user.id" class="admin-users__card">
        <div>
          <h3 class="admin-users__name">{{ user.username }}</h3>
          <p class="admin-users__meta">
            <BaseTag variant="outline">{{ user.role }}</BaseTag>
            <span class="dot"></span>
            <span>创建于 {{ formatDate(user.createdAt) }}</span>
          </p>
        </div>
        <div class="admin-users__actions">
          <BaseButton variant="outline" @click="openEdit(user)">编辑</BaseButton>
          <BaseButton variant="ghost" @click="openDelete(user)">删除</BaseButton>
        </div>
      </article>
    </div>

    <BaseModal v-model="formOpen" :title="isEditing ? '编辑用户' : '新增用户'" eyebrow="Users">
      <form @submit.prevent="handleSubmit" class="admin-users__form">
        <BaseInput
          v-model="form.username"
          label="用户名"
          placeholder="输入用户名"
          :error="errors.username"
        />
        <BaseInput
          v-model="form.password"
          label="密码"
          type="password"
          :placeholder="isEditing ? '留空则不修改密码' : '输入密码'"
          :error="errors.password"
        />
        <BaseSelect
          v-model="form.role"
          label="角色"
          placeholder="选择角色"
          :options="roleOptions"
          :error="errors.role"
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
      <p>确定要删除用户「{{ deleteTarget?.username }}」吗？此操作不可撤销。</p>
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
import BaseSelect, { type SelectOption } from '@/components/base/BaseSelect.vue'
import BaseModal from '@/components/base/BaseModal.vue'
import BaseTag from '@/components/base/BaseTag.vue'
import EmptyState from '@/components/base/EmptyState.vue'
import LoadingState from '@/components/base/LoadingState.vue'
import {
  createUser,
  deleteUser,
  listUsers,
  updateUser,
  type UserProfile,
} from '@/api/users'

const loading = ref(false)
const saving = ref(false)
const deleting = ref(false)
const users = ref<UserProfile[]>([])

const formOpen = ref(false)
const isEditing = ref(false)
const editingId = ref<number | null>(null)
const form = reactive({ username: '', password: '', role: 'USER' as 'USER' | 'ADMIN' })
const errors = reactive({ username: '', password: '', role: '' })

const roleOptions: SelectOption[] = [
  { label: '普通用户', value: 'USER' },
  { label: '管理员', value: 'ADMIN' },
]

const deleteOpen = ref(false)
const deleteTarget = ref<UserProfile | null>(null)

const fetchUsers = async () => {
  loading.value = true
  try {
    users.value = await listUsers()
  } catch {
    users.value = []
    window.dispatchEvent(
      new CustomEvent('toast', {
        detail: { title: '加载失败', message: '无法获取用户列表。', type: 'error' },
      }),
    )
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  form.username = ''
  form.password = ''
  form.role = 'USER'
  errors.username = ''
  errors.password = ''
  errors.role = ''
  isEditing.value = false
  editingId.value = null
}

const openAdd = () => {
  resetForm()
  formOpen.value = true
}

const openEdit = (user: UserProfile) => {
  resetForm()
  isEditing.value = true
  editingId.value = user.id
  form.username = user.username
  form.role = user.role
  formOpen.value = true
}

const validate = (): boolean => {
  let valid = true
  if (!form.username.trim()) {
    errors.username = '请输入用户名'
    valid = false
  } else {
    errors.username = ''
  }
  if (!isEditing.value && form.password.length < 6) {
    errors.password = '密码至少 6 位'
    valid = false
  } else {
    errors.password = ''
  }
  if (!form.role) {
    errors.role = '请选择角色'
    valid = false
  } else {
    errors.role = ''
  }
  return valid
}

const handleSubmit = async () => {
  if (!validate()) return
  saving.value = true
  try {
    if (isEditing.value && editingId.value !== null) {
      await updateUser(editingId.value, {
        username: form.username.trim(),
        password: form.password || undefined,
        role: form.role,
      })
      window.dispatchEvent(
        new CustomEvent('toast', {
          detail: { title: '已更新', message: '用户信息已保存。', type: 'success' },
        }),
      )
    } else {
      await createUser({
        username: form.username.trim(),
        password: form.password,
        role: form.role,
      })
      window.dispatchEvent(
        new CustomEvent('toast', {
          detail: { title: '创建成功', message: '新用户已添加。', type: 'success' },
        }),
      )
    }
    formOpen.value = false
    await fetchUsers()
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

const openDelete = (user: UserProfile) => {
  deleteTarget.value = user
  deleteOpen.value = true
}

const confirmDelete = async () => {
  if (!deleteTarget.value?.id) return
  deleting.value = true
  try {
    await deleteUser(deleteTarget.value.id)
    window.dispatchEvent(
      new CustomEvent('toast', {
        detail: { title: '删除成功', message: '用户已删除。', type: 'success' },
      }),
    )
    deleteOpen.value = false
    await fetchUsers()
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
  fetchUsers()
})
</script>

<style scoped>
.admin-users {
  display: grid;
  gap: 24px;
}

.admin-users__header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
}

.admin-users__eyebrow {
  font-size: 12px;
  letter-spacing: 0.2em;
  text-transform: uppercase;
  color: var(--color-text-muted);
  margin-bottom: 10px;
}

.admin-users__desc {
  color: var(--color-text-muted);
}

.admin-users__list {
  display: grid;
  gap: 14px;
}

.admin-users__card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 18px 20px;
  border-radius: var(--radius-lg);
  background: var(--color-surface);
  box-shadow: var(--shadow-sm);
}

.admin-users__name {
  margin: 0 0 8px;
}

.admin-users__meta {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 12px;
  color: var(--color-text-muted);
}

.dot {
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background: var(--color-primary);
}

.admin-users__actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  flex-shrink: 0;
}

.admin-users__form {
  display: grid;
  gap: 16px;
}

@media (max-width: 900px) {
  .admin-users__header {
    flex-direction: column;
  }

  .admin-users__card {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
