import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { clearToken, getToken, setToken } from '@/utils/auth'
import { login as loginApi, register as registerApi, type LoginRequest, type RegisterRequest } from '@/api/auth'
import { getMe, type UserProfile } from '@/api/users'

type Role = 'USER' | 'ADMIN'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(getToken())
  const user = ref<UserProfile | null>(null)
  const role = ref<Role | null>(null)

  const isAuthenticated = computed(() => Boolean(token.value))
  const isAdmin = computed(() => role.value === 'ADMIN')

  const applyToken = (nextToken: string | null) => {
    if (nextToken) {
      setToken(nextToken)
      token.value = nextToken
      return
    }
    clearToken()
    token.value = null
  }

  const fetchMe = async () => {
    const data = await getMe()
    user.value = data
    role.value = data.role
    return data
  }

  const login = async (payload: LoginRequest) => {
    const data = await loginApi(payload)
    applyToken(data.token)
    role.value = data.role
    user.value = null
    try {
      await fetchMe()
    } catch {
      // Ignore user profile fetch error after login.
    }
    return data
  }

  const register = async (payload: RegisterRequest) => {
    const data = await registerApi(payload)
    applyToken(data.token)
    role.value = data.role
    user.value = null
    try {
      await fetchMe()
    } catch {
      // Ignore user profile fetch error after register.
    }
    return data
  }

  const logout = () => {
    applyToken(null)
    user.value = null
    role.value = null
  }

  const restoreSession = async () => {
    const existingToken = getToken()
    if (!existingToken) {
      applyToken(null)
      return null
    }

    applyToken(existingToken)

    try {
      return await fetchMe()
    } catch {
      logout()
      return null
    }
  }

  return {
    token,
    user,
    role,
    isAuthenticated,
    isAdmin,
    login,
    register,
    fetchMe,
    restoreSession,
    logout,
  }
})
