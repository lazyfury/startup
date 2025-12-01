import axios, { AxiosError } from 'axios'
import type { AxiosRequestHeaders } from 'axios'
import router from '../router'

export const BASE_URL = 'http://127.0.0.1:9002'

export const http = axios.create({ baseURL: BASE_URL, timeout: 15000 })

http.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  const headers = (config.headers || {}) as Record<string, any>
  config.headers = {
    ...headers,
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {})
  } as AxiosRequestHeaders
  return config
})

http.interceptors.response.use(
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
export type Http = typeof http
