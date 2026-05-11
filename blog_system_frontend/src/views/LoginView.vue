<template>
  <section class="login">
    <div class="login__intro">
      <p class="login__eyebrow">Welcome back</p>
      <h1>登录你的灵感港口</h1>
      <p class="login__desc">继续管理你的文章、评论与专属后台。</p>
    </div>

    <form class="login__card" @submit.prevent="handleSubmit">
      <BaseInput v-model="form.username" label="账号" placeholder="输入用户名" :error="errors.username" />
      <BaseInput
        v-model="form.password"
        label="密码"
        placeholder="输入密码"
        type="password"
        :error="errors.password"
      />
      <BaseButton class="login__submit" type="submit" :loading="loading">
        {{ loading ? '登录中' : '登录' }}
      </BaseButton>
      <p class="login__hint">
        还没有账号？
        <RouterLink to="/register">立即注册</RouterLink>
      </p>
    </form>
  </section>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter, useRoute, RouterLink } from 'vue-router'
import BaseInput from '@/components/base/BaseInput.vue'
import BaseButton from '@/components/base/BaseButton.vue'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const loading = ref(false)
const form = reactive({
  username: '',
  password: '',
})

const errors = reactive({
  username: '',
  password: '',
})

const validate = () => {
  errors.username = form.username ? '' : '请输入账号'
  errors.password = form.password ? '' : '请输入密码'
  return !errors.username && !errors.password
}

const handleSubmit = async () => {
  if (!validate()) return
  loading.value = true
  try {
    await authStore.login({ username: form.username, password: form.password })
    const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : '/'
    window.dispatchEvent(
      new CustomEvent('toast', {
        detail: { title: '登录成功', message: '欢迎回来。', type: 'success' },
      }),
    )
    await router.replace(redirect)
  } catch (error) {
    window.dispatchEvent(
      new CustomEvent('toast', {
        detail: { title: '登录失败', message: '账号或密码错误。', type: 'error' },
      }),
    )
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 32px;
  align-items: center;
}

.login__intro {
  padding: 20px 12px;
}

.login__eyebrow {
  font-size: 12px;
  letter-spacing: 0.2em;
  text-transform: uppercase;
  color: var(--color-text-muted);
  margin-bottom: 16px;
}

.login__desc {
  color: var(--color-text-muted);
  max-width: 320px;
}

.login__card {
  display: grid;
  gap: 16px;
  padding: 28px;
  border-radius: var(--radius-lg);
  background: var(--color-surface);
  box-shadow: var(--shadow-md);
}

.login__submit {
  width: 100%;
}

.login__hint {
  margin: 0;
  font-size: 13px;
  color: var(--color-text-muted);
}

.login__hint a {
  font-weight: 600;
}

@media (max-width: 900px) {
  .login {
    grid-template-columns: 1fr;
  }

  .login__intro {
    text-align: center;
  }

  .login__desc {
    margin: 0 auto;
  }
}
</style>
