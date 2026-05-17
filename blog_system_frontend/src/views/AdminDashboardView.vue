<template>
  <section class="dashboard">
    <header class="dashboard__header">
      <div>
        <p class="dashboard__eyebrow">Dashboard</p>
        <h1>仪表盘</h1>
        <p class="dashboard__desc">管理后台概览，快速跳转至各管理模块。</p>
      </div>
    </header>

    <div class="dashboard__grid">
      <RouterLink to="/admin/blogs" class="dashboard__card">
        <h3>博客管理</h3>
        <p>查看、筛选、编辑与删除全站博客。</p>
        <span class="dashboard__arrow">进入</span>
      </RouterLink>
      <RouterLink to="/admin/categories" class="dashboard__card">
        <h3>分类管理</h3>
        <p>维护站点分类，博客可按分类进行归档。</p>
        <span class="dashboard__arrow">进入</span>
      </RouterLink>
      <RouterLink to="/admin/tags" class="dashboard__card">
        <h3>标签管理</h3>
        <p>管理全站标签，便于博客灵活检索。</p>
        <span class="dashboard__arrow">进入</span>
      </RouterLink>
      <RouterLink to="/admin/users" class="dashboard__card">
        <h3>用户管理</h3>
        <p>查看、新增、编辑与删除用户账号。</p>
        <span class="dashboard__arrow">进入</span>
      </RouterLink>
    </div>
  </section>
</template>

<script setup lang="ts">
import { RouterLink } from 'vue-router'
</script>

<style scoped>
.dashboard {
  display: grid;
  gap: 24px;
}

.dashboard__header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
}

.dashboard__eyebrow {
  font-size: 12px;
  letter-spacing: 0.2em;
  text-transform: uppercase;
  color: var(--color-text-muted);
  margin-bottom: 10px;
}

.dashboard__desc {
  color: var(--color-text-muted);
}

.dashboard__grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 18px;
}

.dashboard__card {
  display: grid;
  gap: 12px;
  padding: 28px;
  border-radius: var(--radius-lg);
  background: var(--color-surface);
  box-shadow: var(--shadow-sm);
  text-decoration: none;
  color: var(--color-text);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.dashboard__card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.dashboard__card h3 {
  margin: 0;
  color: var(--color-heading);
}

.dashboard__card p {
  margin: 0;
  color: var(--color-text-muted);
}

.dashboard__arrow {
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.12em;
  color: var(--color-primary-strong);
}
</style>
