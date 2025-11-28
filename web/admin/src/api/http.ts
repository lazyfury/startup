export const BASE_URL = 'http://127.0.0.1:9002'

const defaultHeaders = () => {
  const token = localStorage.getItem('token')
  return {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {})
  } as Record<string, string>
}

export function request(path: string, init: RequestInit = {}) {
  const headers = { ...defaultHeaders(), ...(init.headers || {}) }
  return fetch(`${BASE_URL}${path}`, { ...init, headers })
}
