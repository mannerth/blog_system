<template>
  <section class="register">
    <div class="register__intro">
      <p class="register__eyebrow">Create account</p>
      <h1>开启你的蓝绿写作档案</h1>
      <p class="register__desc">注册后即可创建文章与管理个人信息。</p>
    </div>

    <form class="register__card" @submit.prevent="handleSubmit">
      <BaseInput v-model="form.username" label="账号" placeholder="设定用户名" :error="errors.username" />
      <BaseInput
        v-model="form.password"
        label="密码"
        placeholder="设置密码"
        type="password"
        :error="errors.password"
      />
      <BaseInput
        v-model="form.confirm"
        label="确认密码"
        placeholder="再次输入密码"
        type="password"
        :error="errors.confirm"
      />
      <BaseButton class="register__submit" type="submit" :loading="loading">
        {{ loading ? '注册中' : '注册并登录' }}
      </BaseButton>
      <p class="register__hint">
        已有账号？
        <RouterLink to="/login">直接登录</RouterLink>
      </p>
    </form>
  </section>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter, RouterLink } from 'vue-router'
import BaseInput from '@/components/base/BaseInput.vue'
import BaseButton from '@/components/base/BaseButton.vue'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const loading = ref(false)
const form = reactive({
  username: '',
  password: '',
  confirm: '',
})

const errors = reactive({
  username: '',
  password: '',
  confirm: '',
})

const validate = () => {
  errors.username = form.username ? '' : '请输入账号'
  errors.password = form.password ? '' : '请输入密码'
  errors.confirm = form.confirm ? '' : '请再次输入密码'
  if (!errors.password && !errors.confirm && form.password !== form.confirm) {
    errors.confirm = '两次密码不一致'
  }
  return !errors.username && !errors.password && !errors.confirm
}

const handleSubmit = async () => {
  if (!validate()) return
  loading.value = true
  try {
    await authStore.register({ username: form.username, password: form.password })
    window.dispatchEvent(
      new CustomEvent('toast', {
        detail: { title: '注册成功', message: '已自动登录。', type: 'success' },
      }),
    )
    await router.replace('/profile')
  } catch (error) {
    window.dispatchEvent(
      new CustomEvent('toast', {
        detail: { title: '注册失败', message: '请稍后再试。', type: 'error' },
      }),
    )
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 32px;
  align-items: center;
}

.register__intro {
  padding: 20px 12px;
}

.register__eyebrow {
  font-size: 12px;
  letter-spacing: 0.2em;
  text-transform: uppercase;
  color: var(--color-text-muted);
  margin-bottom: 16px;
}

.register__desc {
  color: var(--color-text-muted);
  max-width: 320px;
}

.register__card {
  display: grid;
  gap: 16px;
  padding: 28px;
  border-radius: var(--radius-lg);
  background: var(--color-surface);
  box-shadow: var(--shadow-md);
}

.register__submit {
  width: 100%;
}

.register__hint {
  margin: 0;
  font-size: 13px;
  color: var(--color-text-muted);
}

.register__hint a {
  font-weight: 600;
}

@media (max-width: 900px) {
  .register {
    grid-template-columns: 1fr;
  }

  .register__intro {
    text-align: center;
  }

  .register__desc {
    margin: 0 auto;
  }
}
</style>
