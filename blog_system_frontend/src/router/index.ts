import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
      meta: {
        title: '首页',
        description: '博客列表与最新动态将在这里呈现。',
      },
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/PlaceholderView.vue'),
      meta: {
        title: '登录',
        description: '请登录以继续访问个人功能。',
      },
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/PlaceholderView.vue'),
      meta: {
        title: '注册',
        description: '创建账号，开始记录你的灵感。',
      },
    },
    {
      path: '/blogs/:id',
      name: 'blog-detail',
      component: () => import('../views/PlaceholderView.vue'),
      meta: {
        title: '博客详情',
        description: '内容详情将在这里展示。',
      },
    },
    {
      path: '/editor',
      name: 'blog-create',
      component: () => import('../views/PlaceholderView.vue'),
      meta: {
        title: '发布博客',
        description: '开始撰写新的文章。',
      },
    },
    {
      path: '/editor/:id',
      name: 'blog-edit',
      component: () => import('../views/PlaceholderView.vue'),
      meta: {
        title: '编辑博客',
        description: '继续完善你的文章内容。',
      },
    },
    {
      path: '/my-blogs',
      name: 'my-blogs',
      component: () => import('../views/PlaceholderView.vue'),
      meta: {
        title: '我的博客',
        description: '管理你发布的内容。',
      },
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('../views/PlaceholderView.vue'),
      meta: {
        title: '个人中心',
        description: '查看与更新个人信息。',
      },
    },
    {
      path: '/admin',
      name: 'admin-dashboard',
      component: () => import('../views/PlaceholderView.vue'),
      meta: {
        title: '管理后台',
        description: '管理员可以在此查看全站数据。',
      },
    },
    {
      path: '/admin/blogs',
      name: 'admin-blogs',
      component: () => import('../views/PlaceholderView.vue'),
      meta: {
        title: '博客管理',
        description: '筛选、编辑与管理博客。',
      },
    },
    {
      path: '/admin/categories',
      name: 'admin-categories',
      component: () => import('../views/PlaceholderView.vue'),
      meta: {
        title: '分类管理',
        description: '维护站点分类。',
      },
    },
    {
      path: '/admin/tags',
      name: 'admin-tags',
      component: () => import('../views/PlaceholderView.vue'),
      meta: {
        title: '标签管理',
        description: '管理全站标签。',
      },
    },
    {
      path: '/admin/users',
      name: 'admin-users',
      component: () => import('../views/PlaceholderView.vue'),
      meta: {
        title: '用户管理',
        description: '查看与管理用户信息。',
      },
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: () => import('../views/PlaceholderView.vue'),
      meta: {
        title: '页面未找到',
        description: '你访问的页面不存在。',
      },
    },
  ],
})

export default router
