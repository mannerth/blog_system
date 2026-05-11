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

setupRouterGuards(router)
setupAuthEvents(router, {
  onUnauthorized: () => window.alert('登录已失效，请重新登录。'),
  onForbidden: () => window.alert('当前账号暂无权限访问此资源。'),
})

const authStore = useAuthStore(pinia)
authStore.restoreSession()

app.mount('#app')
