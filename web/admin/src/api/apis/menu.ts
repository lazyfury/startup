import { request } from '../http'
import { API } from '../endpoints'
import type { ApiResponse, MenuItem, Permission } from '../types'

export const MenuApi = {
  async tree(): Promise<ApiResponse<MenuItem[]>> {
    const res = await request(API.menu.tree)
    return res.json()
  },
  async list(): Promise<ApiResponse<MenuItem[]>> {
    const res = await request(API.menu.list)
    return res.json()
  },
  async get(id: number): Promise<ApiResponse<MenuItem>> {
    const res = await request(API.menu.item(id))
    return res.json()
  },
  async create(body: MenuItem): Promise<ApiResponse<MenuItem>> {
    const res = await request(API.menu.list, {
      method: 'POST',
      body: JSON.stringify(body)
    })
    return res.json()
  },
  async update(id: number, body: MenuItem): Promise<ApiResponse<MenuItem>> {
    const res = await request(API.menu.item(id), {
      method: 'PUT',
      body: JSON.stringify(body)
    })
    return res.json()
  },
  async remove(id: number): Promise<Response> {
    return request(API.menu.item(id), { method: 'DELETE' })
  },
  async getPermissions(id: number, params?: { scopeType?: string; scopeId?: number | null }): Promise<ApiResponse<Permission[]>> {
    const qp = params ? `?${new URLSearchParams(Object.entries(params).filter(([_, v]) => v !== undefined && v !== null) as any).toString()}` : ''
    const res = await request(`${API.menu.permissions(id)}${qp}`)
    return res.json()
  },
  async autoCreatePermissions(id: number, payload: { scopeType?: string; scopeId?: number | null; actions?: string[] } = {}): Promise<ApiResponse<any>> {
    const res = await request(API.menu.autoPermissions(id), {
      method: 'POST',
      body: JSON.stringify(payload)
    })
    return res.json()
  }
}
