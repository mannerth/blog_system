const TOKEN_KEY = 'blog_system_token'

export const getToken = (): string | null => {
  return localStorage.getItem(TOKEN_KEY)
}

export const setToken = (token: string): void => {
  if (token) {
    localStorage.setItem(TOKEN_KEY, token)
    return
  }
  localStorage.removeItem(TOKEN_KEY)
}

export const clearToken = (): void => {
  localStorage.removeItem(TOKEN_KEY)
}
