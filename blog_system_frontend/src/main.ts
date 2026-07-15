import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import { setupRouterGuards } from './router/guards'
import { useAuthStore } from './stores/auth'
import { setupAuthEvents } from './utils/auth-events'

const app = createApp(App)

const pinia = createPinia()

app.use(pinia)
app.use(router)

const dispatchToast = (payload: { title: string; message?: string; type?: 'success' | 'error' | 'warning' }) => {
  window.dispatchEvent(new CustomEvent('toast', { detail: payload }))
}

setupRouterGuards(router)
setupAuthEvents(router, {
  onUnauthorized: () =>
    dispatchToast({ title: '登录已失效', message: '请重新登录继续操作。', type: 'warning' }),
  onForbidden: () =>
    dispatchToast({ title: '无访问权限', message: '当前账号暂无法访问该资源。', type: 'error' }),
})

const authStore = useAuthStore(pinia)
authStore.restoreSession()

app.mount('#app')
