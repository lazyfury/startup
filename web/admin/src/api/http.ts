import axios, { AxiosError } from 'axios'
import type { AxiosResponse, AxiosRequestHeaders } from 'axios'
import router from '../router'

export const BASE_URL = 'http://127.0.0.1:9002'

const instance = axios.create({ baseURL: BASE_URL, timeout: 15000 })

instance.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  const headers = (config.headers || {}) as Record<string, any>
  config.headers = {
    ...headers,
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {})
  } as AxiosRequestHeaders
  return config
})

instance.interceptors.response.use(
  (resp) => resp,
  (error: AxiosError) => {
    const status = error.response?.status
    if (status === 401) {
      try { localStorage.removeItem('token') } catch {}
      router.push("/login")
    }
    return Promise.reject(error)
  }
)

export function request(path: string, init: RequestInit = {}) {
  const method = (init.method || 'GET').toUpperCase() as any
  const headers = init.headers as Record<string, string> | undefined
  const data = (init as any).body ?? undefined
  return instance.request({ url: path, method, headers, data })
    .then((res: AxiosResponse) => ({
      ok: res.status >= 200 && res.status < 300,
      status: res.status,
      json: async () => res.data,
      text: async () => (typeof res.data === 'string' ? res.data : JSON.stringify(res.data))
    }))
    .catch((err: AxiosError) => {
      const res = err.response
      const status = res?.status ?? 0
      const data = res?.data
      return {
        ok: status >= 200 && status < 300,
        status,
        json: async () => data,
        text: async () => (typeof data === 'string' ? data : JSON.stringify(data ?? ''))
      }
    })
}
