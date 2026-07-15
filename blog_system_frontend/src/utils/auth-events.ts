import type { Router } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

interface AuthEventOptions {
  onUnauthorized?: () => void
  onForbidden?: () => void
}

export const setupAuthEvents = (router: Router, options: AuthEventOptions = {}) => {
  const handleUnauthorized = () => {
    const authStore = useAuthStore()
    authStore.logout()
    if (options.onUnauthorized) {
      options.onUnauthorized()
    }
    const current = router.currentRoute.value
    if (current.path !== '/login') {
      router.replace({
        path: '/login',
        query: { redirect: current.fullPath },
      })
    }
  }

  const handleForbidden = () => {
    if (options.onForbidden) {
      options.onForbidden()
    }
    const current = router.currentRoute.value
    if (current.path === '/login') return
    router.back()
  }

  window.addEventListener('auth:unauthorized', handleUnauthorized)
  window.addEventListener('auth:forbidden', handleForbidden)
}
