<template>
  <section class="profile">
    <div class="profile__header">
      <div>
        <p class="profile__eyebrow">Profile</p>
        <h1>个人中心</h1>
        <p class="profile__desc">查看你的账户信息与角色。</p>
      </div>
      <BaseButton variant="outline" @click="handleLogout">退出登录</BaseButton>
    </div>

    <div class="profile__card">
      <LoadingState v-if="loading" />
      <EmptyState v-else-if="!profile" title="暂无用户信息" description="请稍后刷新或重新登录。" />
      <div v-else class="profile__grid">
        <div class="profile__item">
          <span class="profile__label">用户名</span>
          <span class="profile__value">{{ profile.username }}</span>
        </div>
        <div class="profile__item">
          <span class="profile__label">角色</span>
          <BaseTag>{{ profile.role }}</BaseTag>
        </div>
        <div class="profile__item">
          <span class="profile__label">注册时间</span>
          <span class="profile__value">{{ profile.createdAt }}</span>
        </div>
      </div>
    </div>

    <form class="profile__card" @submit.prevent="handleUpdate" v-if="profile">
      <div class="profile__form-header">
        <div>
          <p class="profile__eyebrow">Update</p>
          <h2>更新资料</h2>
        </div>
        <BaseButton variant="ghost" type="submit" :loading="saving">
          {{ saving ? '保存中' : '保存修改' }}
        </BaseButton>
      </div>
      <BaseInput
        v-model="editForm.username"
        label="用户名"
        placeholder="输入新的用户名"
        :error="editErrors.username"
      />
      <BaseInput
        v-model="editForm.password"
        label="新密码"
        placeholder="留空则不修改密码"
        type="password"
        :error="editErrors.password"
      />
    </form>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import BaseButton from '@/components/base/BaseButton.vue'
import BaseInput from '@/components/base/BaseInput.vue'
import BaseTag from '@/components/base/BaseTag.vue'
import EmptyState from '@/components/base/EmptyState.vue'
import LoadingState from '@/components/base/LoadingState.vue'
import { useAuthStore } from '@/stores/auth'
import { updateMe } from '@/api/users'

const authStore = useAuthStore()
const router = useRouter()

const loading = ref(false)
const saving = ref(false)
const profile = computed(() => authStore.user)
const editForm = reactive({
  username: '',
  password: '',
})
const editErrors = reactive({
  username: '',
  password: '',
})

const loadProfile = async () => {
  loading.value = true
  try {
    await authStore.fetchMe()
  } catch {
    window.dispatchEvent(
      new CustomEvent('toast', {
        detail: { title: '获取用户失败', message: '请稍后重试。', type: 'error' },
      }),
    )
  } finally {
    loading.value = false
  }
}

const validateUpdate = () => {
  editErrors.username = editForm.username ? '' : '请输入用户名'
  if (editForm.password && editForm.password.length < 6) {
    editErrors.password = '密码至少 6 位'
  } else {
    editErrors.password = ''
  }
  return !editErrors.username && !editErrors.password
}

const handleUpdate = async () => {
  if (!validateUpdate()) return
  saving.value = true
  try {
    const payload = {
      username: editForm.username,
      password: editForm.password ? editForm.password : undefined,
    }
    await updateMe(payload)
    await authStore.fetchMe()
    editForm.password = ''
    window.dispatchEvent(
      new CustomEvent('toast', {
        detail: { title: '资料已更新', message: '修改已保存。', type: 'success' },
      }),
    )
  } catch {
    window.dispatchEvent(
      new CustomEvent('toast', {
        detail: { title: '更新失败', message: '请稍后再试。', type: 'error' },
      }),
    )
  } finally {
    saving.value = false
  }
}

const handleLogout = () => {
  authStore.logout()
  window.dispatchEvent(
    new CustomEvent('toast', {
      detail: { title: '已退出登录', message: '期待再次见到你。', type: 'warning' },
    }),
  )
  router.replace('/')
}

onMounted(() => {
  if (!profile.value) {
    loadProfile()
  }
})

watch(
  () => profile.value,
  (next) => {
    editForm.username = next?.username ?? ''
  },
  { immediate: true },
)
</script>

<style scoped>
.profile {
  display: grid;
  gap: 24px;
}

.profile__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.profile__eyebrow {
  font-size: 12px;
  letter-spacing: 0.2em;
  text-transform: uppercase;
  color: var(--color-text-muted);
  margin-bottom: 10px;
}

.profile__desc {
  color: var(--color-text-muted);
}

.profile__card {
  padding: 24px;
  border-radius: var(--radius-lg);
  background: var(--color-surface);
  box-shadow: var(--shadow-md);
}

.profile__form-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.profile__grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 18px;
}

.profile__item {
  display: grid;
  gap: 6px;
}

.profile__label {
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.12em;
  color: var(--color-text-muted);
}

.profile__value {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-heading);
}

@media (max-width: 640px) {
  .profile__header {
    flex-direction: column;
    align-items: flex-start;
  }

  .profile__form-header {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
