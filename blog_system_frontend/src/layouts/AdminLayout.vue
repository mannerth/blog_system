<template>
  <div class="admin-layout">
    <aside class="admin-sidebar">
      <div class="admin-brand">
        <span class="admin-brand__mark">后台</span>
        <span class="admin-brand__name">Control Deck</span>
      </div>
      <nav class="admin-nav">
        <RouterLink to="/admin" class="admin-nav__link">仪表盘</RouterLink>
        <RouterLink to="/admin/blogs" class="admin-nav__link">博客管理</RouterLink>
        <RouterLink to="/admin/categories" class="admin-nav__link">分类管理</RouterLink>
        <RouterLink to="/admin/tags" class="admin-nav__link">标签管理</RouterLink>
        <RouterLink to="/admin/users" class="admin-nav__link">用户管理</RouterLink>
      </nav>
      <div class="admin-sidebar__footer">
        <RouterLink to="/" class="admin-return">返回前台</RouterLink>
      </div>
    </aside>

    <div class="admin-content">
      <header class="admin-topbar">
        <div class="admin-topbar__title">
          <span>管理中心</span>
          <small>Blue Green Blog</small>
        </div>
        <div class="admin-topbar__actions">
          <span class="admin-topbar__user">{{ userName }}</span>
          <button class="admin-ghost" type="button" @click="handleLogout">退出登录</button>
        </div>
      </header>

      <main class="admin-main">
        <slot />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const userName = computed(() => authStore.user?.username ?? '管理员')

const handleLogout = () => {
  authStore.logout()
  window.dispatchEvent(
    new CustomEvent('toast', {
      detail: { title: '已退出登录', message: '期待再次见到你。', type: 'warning' },
    }),
  )
  router.replace('/')
}
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 260px 1fr;
  background: var(--color-background);
}

.admin-sidebar {
  position: sticky;
  top: 0;
  height: 100vh;
  display: flex;
  flex-direction: column;
  padding: 32px 24px;
  background: var(--color-surface);
  border-right: 1px solid var(--color-border);
}

.admin-brand {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 32px;
}

.admin-brand__mark {
  font-family: var(--font-display);
  font-size: 20px;
  color: var(--color-heading);
}

.admin-brand__name {
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.18em;
  color: var(--color-text-muted);
}

.admin-nav {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.admin-nav__link {
  padding: 12px 14px;
  border-radius: var(--radius-sm);
  color: var(--color-text);
  background: transparent;
  transition: background 0.2s ease, transform 0.2s ease;
}

.admin-nav__link:hover,
.admin-nav__link.router-link-active {
  background: var(--color-primary-soft);
  color: var(--color-primary-strong);
  transform: translateX(4px);
}

.admin-sidebar__footer {
  margin-top: auto;
}

.admin-return {
  display: inline-flex;
  padding: 10px 14px;
  border-radius: var(--radius-sm);
  border: 1px solid rgba(28, 155, 138, 0.25);
  color: var(--color-primary-strong);
}

.admin-content {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.admin-topbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 32px;
  border-bottom: 1px solid var(--color-border);
  background: rgba(243, 248, 247, 0.8);
  backdrop-filter: blur(12px);
}

.admin-topbar__title {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 18px;
  color: var(--color-heading);
}

.admin-topbar__title small {
  font-size: 12px;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: var(--color-text-muted);
}

.admin-topbar__actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.admin-topbar__user {
  font-size: 14px;
  color: var(--color-text-muted);
}

.admin-ghost {
  padding: 10px 16px;
  border-radius: var(--radius-sm);
  border: 1px solid rgba(28, 155, 138, 0.3);
  background: transparent;
  color: var(--color-primary-strong);
  cursor: pointer;
}

.admin-main {
  padding: 32px;
}

@media (max-width: 1024px) {
  .admin-layout {
    grid-template-columns: 1fr;
  }

  .admin-sidebar {
    position: relative;
    height: auto;
    flex-direction: row;
    align-items: center;
    gap: 16px;
    padding: 18px 20px;
    overflow-x: auto;
  }

  .admin-brand {
    margin-bottom: 0;
  }

  .admin-nav {
    flex-direction: row;
    gap: 10px;
  }

  .admin-sidebar__footer {
    margin-top: 0;
  }

  .admin-topbar {
    padding: 20px;
  }

  .admin-main {
    padding: 24px 20px 40px;
  }
}

@media (max-width: 640px) {
  .admin-topbar {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
}
</style>
