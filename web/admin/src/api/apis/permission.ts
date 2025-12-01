import { http } from '../http'
import { API } from '../endpoints'
import type { ApiResponse, Page, Permission } from '../types'

export const PermissionApi = {
  async list(params: { page: number; size: number }): Promise<ApiResponse<Page<Permission>>> {
    const { data } = await http.get(API.permissions.list, { params })
    return data
  },
  async get(id: number): Promise<ApiResponse<Permission>> {
    const { data } = await http.get(API.permissions.item(id))
    return data
  },
  async create(body: Permission): Promise<ApiResponse<Permission>> {
    const { data } = await http.post(API.permissions.list, body)
    return data
  },
  async update(id: number, body: Permission): Promise<ApiResponse<Permission>> {
    const { data } = await http.put(API.permissions.item(id), body)
    return data
  },
  async remove(id: number) {
    return http.delete(API.permissions.item(id))
  },
  async tree(params?: { scopeType?: string; scopeId?: number | null }): Promise<ApiResponse<Permission[]>> {
    const { data } = await http.get(API.permissions.tree, { params })
    return data
  },
  async tags(params?: { scopeType?: string; scopeId?: number | null }): Promise<ApiResponse<string[]>> {
    const { data } = await http.get(API.permissions.tags, { params })
    return data
  },
  async searchByTag(params: { q: string; scopeType?: string; scopeId?: number | null; limit?: number }): Promise<ApiResponse<Permission[]>> {
    const { data } = await http.get(API.permissions.searchByTag, { params })
    return data
  }
}
