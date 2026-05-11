import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import { setupRouterGuards } from './router/guards'

const app = createApp(App)

app.use(createPinia())
app.use(router)

setupRouterGuards(router)

app.mount('#app')
