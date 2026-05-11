export class ApiError extends Error {
  status: number
  data?: unknown

  constructor(message: string, status: number, data?: unknown) {
    super(message)
    this.status = status
    this.data = data
  }
}

export type HttpMethod = 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE'

export interface RequestOptions<TBody = unknown> {
  method?: HttpMethod
  params?: Record<string, string | number | boolean | null | undefined>
  body?: TBody
  headers?: Record<string, string>
  signal?: AbortSignal
  withAuth?: boolean
}

const BASE_URL = import.meta.env.VITE_API_BASE_URL ?? ''
const DEFAULT_TIMEOUT = Number(import.meta.env.VITE_API_TIMEOUT ?? 15000)

const serializeParams = (params?: RequestOptions['params']): string => {
  if (!params) return ''
  const search = new URLSearchParams()
  Object.entries(params).forEach(([key, value]) => {
    if (value === undefined || value === null) return
    search.append(key, String(value))
  })
  const query = search.toString()
  return query ? `?${query}` : ''
}

const createTimeoutSignal = (timeoutMs: number, signal?: AbortSignal) => {
  const controller = new AbortController()
  const timeoutId = window.setTimeout(() => controller.abort(), timeoutMs)
  const cleanup = () => window.clearTimeout(timeoutId)

  if (!signal) {
    return { signal: controller.signal, cleanup }
  }

  if (signal.aborted) {
    controller.abort()
    cleanup()
    return { signal: controller.signal, cleanup }
  }

  signal.addEventListener('abort', () => controller.abort(), { once: true })
  return { signal: controller.signal, cleanup }
}

const toJson = async <T>(response: Response): Promise<T> => {
  const text = await response.text()
  if (!text) return undefined as T
  try {
    return JSON.parse(text) as T
  } catch {
    return text as T
  }
}

const buildUrl = (path: string, params?: RequestOptions['params']) => {
  if (path.startsWith('http://') || path.startsWith('https://')) {
    return `${path}${serializeParams(params)}`
  }

  const base = BASE_URL.endsWith('/') ? BASE_URL.slice(0, -1) : BASE_URL
  const normalizedPath = path.startsWith('/') ? path : `/${path}`
  return `${base}${normalizedPath}${serializeParams(params)}`
}

export const request = async <TResponse = unknown, TBody = unknown>(
  path: string,
  options: RequestOptions<TBody> = {},
): Promise<TResponse> => {
  const {
    method = 'GET',
    params,
    body,
    headers,
    signal,
    withAuth = true,
  } = options

  const url = buildUrl(path, params)
  const finalHeaders: Record<string, string> = {
    'Content-Type': 'application/json',
    ...headers,
  }

  if (withAuth) {
    const { getToken } = await import('./auth')
    const token = getToken()
    if (token) {
      finalHeaders.Authorization = token.startsWith('Bearer ') ? token : `Bearer ${token}`
    }
  }

  const { signal: timeoutSignal, cleanup } = createTimeoutSignal(DEFAULT_TIMEOUT, signal)

  try {
    const response = await fetch(url, {
      method,
      headers: finalHeaders,
      body: body === undefined ? undefined : JSON.stringify(body),
      signal: timeoutSignal,
    })

    const data = await toJson<TResponse>(response)

    if (!response.ok) {
      throw new ApiError(response.statusText || 'Request failed', response.status, data)
    }

    return data
  } catch (error) {
    if (error instanceof ApiError) {
      if (error.status === 401) {
        window.dispatchEvent(new CustomEvent('auth:unauthorized', { detail: error }))
      }
      if (error.status === 403) {
        window.dispatchEvent(new CustomEvent('auth:forbidden', { detail: error }))
      }
      throw error
    }
    if (error instanceof DOMException && error.name === 'AbortError') {
      throw new ApiError('Request timeout', 408)
    }
    throw error
  } finally {
    cleanup()
  }
}

export const http = {
  get: <TResponse>(path: string, options?: Omit<RequestOptions, 'method' | 'body'>) =>
    request<TResponse>(path, { ...options, method: 'GET' }),
  post: <TResponse, TBody>(path: string, body?: TBody, options?: Omit<RequestOptions<TBody>, 'method' | 'body'>) =>
    request<TResponse, TBody>(path, { ...options, method: 'POST', body }),
  put: <TResponse, TBody>(path: string, body?: TBody, options?: Omit<RequestOptions<TBody>, 'method' | 'body'>) =>
    request<TResponse, TBody>(path, { ...options, method: 'PUT', body }),
  patch: <TResponse, TBody>(path: string, body?: TBody, options?: Omit<RequestOptions<TBody>, 'method' | 'body'>) =>
    request<TResponse, TBody>(path, { ...options, method: 'PATCH', body }),
  delete: <TResponse>(path: string, options?: Omit<RequestOptions, 'method' | 'body'>) =>
    request<TResponse>(path, { ...options, method: 'DELETE' }),
}
