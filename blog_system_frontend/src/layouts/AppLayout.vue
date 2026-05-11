<template>
  <div class="app-layout">
    <header class="app-header">
      <div class="app-header__inner">
        <RouterLink to="/" class="brand">
          <span class="brand__mark">潮汐</span>
          <span class="brand__name">Blue Green Blog</span>
        </RouterLink>

        <div class="search">
          <span class="search__label">搜索</span>
          <input class="search__input" type="search" placeholder="试试搜索标题或标签" />
        </div>

        <div class="user-area">
          <RouterLink class="ghost-button" to="/login">登录</RouterLink>
          <RouterLink class="solid-button" to="/register">注册</RouterLink>
        </div>
      </div>
    </header>

    <main class="app-main">
      <div class="app-main__inner">
        <slot />
      </div>
    </main>

    <footer class="app-footer">
      <div class="app-footer__inner">
        <p class="app-footer__title">把想法写成一条条清晰的浪线</p>
        <div class="app-footer__meta">
          <span>把我们衣服纽扣互扣 那就不用分离</span>
          <span class="dot"></span>
          <span>Blog System</span>
          <span class="dot"></span>
          <span>2026</span>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { RouterLink } from 'vue-router'
</script>

<style scoped>
.app-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.app-header {
  position: sticky;
  top: 0;
  z-index: 10;
  background: rgba(243, 248, 247, 0.92);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid var(--color-border);
}

.app-header__inner {
  max-width: var(--content-width);
  margin: 0 auto;
  padding: 20px var(--page-padding);
  display: flex;
  align-items: center;
  gap: 24px;
}

.brand {
  display: flex;
  flex-direction: column;
  text-decoration: none;
  color: var(--color-heading);
}

.brand__mark {
  font-family: var(--font-display);
  font-size: 22px;
}

.brand__name {
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.12em;
  color: var(--color-text-muted);
}

.search {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 16px;
  border-radius: var(--radius-md);
  background: var(--color-surface);
  box-shadow: var(--shadow-sm);
}

.search__label {
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.12em;
  color: var(--color-text-muted);
}

.search__input {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
  font-size: 14px;
}

.user-area {
  display: flex;
  align-items: center;
  gap: 12px;
}

.ghost-button,
.solid-button {
  padding: 10px 18px;
  border-radius: var(--radius-sm);
  font-size: 14px;
  text-decoration: none;
  transition: transform 0.2s ease, box-shadow 0.2s ease, background 0.2s ease;
}

.ghost-button {
  color: var(--color-primary-strong);
  border: 1px solid rgba(28, 155, 138, 0.3);
  background: transparent;
}

.solid-button {
  color: #ffffff;
  background: linear-gradient(120deg, var(--color-primary), #3ab7a0);
  box-shadow: var(--shadow-sm);
}

.ghost-button:hover,
.solid-button:hover {
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

.app-main {
  flex: 1;
}

.app-main__inner {
  max-width: var(--content-width);
  margin: 0 auto;
  padding: 32px var(--page-padding) 64px;
}

.app-footer {
  background: var(--color-surface);
  border-top: 1px solid var(--color-border);
}

.app-footer__inner {
  max-width: var(--content-width);
  margin: 0 auto;
  padding: 28px var(--page-padding);
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.app-footer__title {
  font-family: var(--font-display);
  color: var(--color-heading);
}

.app-footer__meta {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 12px;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: var(--color-text-muted);
}

.dot {
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background: var(--color-primary);
}

@media (max-width: 900px) {
  .app-header__inner {
    flex-direction: column;
    align-items: stretch;
  }

  .brand {
    align-items: center;
  }

  .user-area {
    justify-content: center;
  }
}
</style>
