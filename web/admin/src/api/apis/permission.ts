import { request } from '../http'
import { API } from '../endpoints'
import type { ApiResponse, Page, Permission } from '../types'

export const PermissionApi = {
  async list(params: { page: number; size: number }): Promise<ApiResponse<Page<Permission>>> {
    const qp = `?page=${params.page}&size=${params.size}`
    const res = await request(`${API.permissions.list}${qp}`)
    return res.json()
  },
  async get(id: number): Promise<ApiResponse<Permission>> {
    const res = await request(API.permissions.item(id))
    return res.json()
  },
  async create(body: Permission): Promise<ApiResponse<Permission>> {
    const res = await request(API.permissions.list, { method: 'POST', body: JSON.stringify(body) })
    return res.json()
  },
  async update(id: number, body: Permission): Promise<ApiResponse<Permission>> {
    const res = await request(API.permissions.item(id), { method: 'PUT', body: JSON.stringify(body) })
    return res.json()
  },
  async remove(id: number): Promise<Response> {
    return request(API.permissions.item(id), { method: 'DELETE' })
  },
  async tree(): Promise<ApiResponse<Permission[]>> {
    const res = await request(API.permissions.tree)
    return res.json()
  },
  async tags(params?: { scopeType?: string; scopeId?: number | null }): Promise<ApiResponse<string[]>> {
    const qp: string[] = []
    if (params?.scopeType) qp.push(`scopeType=${params.scopeType}`)
    if (params?.scopeId !== undefined && params?.scopeId !== null) qp.push(`scopeId=${params.scopeId}`)
    const res = await request(`${API.permissions.tags}${qp.length ? `?${qp.join('&')}` : ''}`)
    return res.json()
  },
  async searchByTag(params: { q: string; scopeType?: string; scopeId?: number | null; limit?: number }): Promise<ApiResponse<Permission[]>> {
    const qp: string[] = [`q=${encodeURIComponent(params.q || '')}`]
    if (params.scopeType) qp.push(`scopeType=${params.scopeType}`)
    if (params.scopeId !== undefined && params.scopeId !== null) qp.push(`scopeId=${params.scopeId}`)
    if (params.limit) qp.push(`limit=${params.limit}`)
    const res = await request(`${API.permissions.searchByTag}?${qp.join('&')}`)
    return res.json()
  }
}
