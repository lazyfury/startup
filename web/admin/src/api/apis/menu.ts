import { http } from '../http'
import { API } from '../endpoints'
import type { ApiResponse, MenuItem, Permission } from '../types'

export const MenuApi = {
  async tree(params?: { includeDisabled?: boolean }): Promise<ApiResponse<MenuItem[]>> {
    const { data } = await http.get(API.menu.tree, { params })
    return data
  },
  async list(): Promise<ApiResponse<MenuItem[]>> {
    const { data } = await http.get(API.menu.list)
    return data
  },
  async get(id: number): Promise<ApiResponse<MenuItem>> {
    const { data } = await http.get(API.menu.item(id))
    return data
  },
  async create(body: MenuItem): Promise<ApiResponse<MenuItem>> {
    const { data } = await http.post(API.menu.list, body)
    return data
  },
  async update(id: number, body: MenuItem): Promise<ApiResponse<MenuItem>> {
    const { data } = await http.put(API.menu.item(id), body)
    return data
  },
  async remove(id: number) {
    return http.delete(API.menu.item(id))
  },
  async getPermissions(id: number, params?: { scopeType?: string; scopeId?: number | null }): Promise<ApiResponse<Permission[]>> {
    const { data } = await http.get(API.menu.permissions(id), { params })
    return data
  },
  async autoCreatePermissions(id: number, payload: { scopeType?: string; scopeId?: number | null; actions?: string[] } = {}): Promise<ApiResponse<any>> {
    const { data } = await http.post(API.menu.autoPermissions(id), payload)
    return data
  }
}
