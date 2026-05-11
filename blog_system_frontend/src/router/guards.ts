import type { Router } from 'vue-router'
import { getToken, clearToken } from '@/utils/auth'
import { getMe } from '@/api'

export const setupRouterGuards = (router: Router) => {
  router.beforeEach(async (to) => {
    const requiresAuth = Boolean(to.meta.requiresAuth)
    const requiresAdmin = Boolean(to.meta.requiresAdmin)
    const token = getToken()

    if (!requiresAuth && !requiresAdmin) return true

    if (!token) {
      return {
        path: '/login',
        query: { redirect: to.fullPath },
      }
    }

    if (!requiresAdmin) return true

    try {
      const me = await getMe()
      if (me.role === 'ADMIN') return true
    } catch {
      clearToken()
      return {
        path: '/login',
        query: { redirect: to.fullPath },
      }
    }

    return { path: '/' }
  })
}
