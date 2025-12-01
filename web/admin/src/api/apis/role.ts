import { http } from '../http'
import { API } from '../endpoints'
import type { ApiResponse, Page, Role } from '../types'

export const RoleApi = {
  async list(params: { page: number; size: number; scopeId?: number | null }): Promise<ApiResponse<Page<Role>>> {
    const { data } = await http.get(API.roles.list, { params: { page: params.page, size: params.size, scopeId: params.scopeId } })
    return data
  },
  async get(id: number): Promise<ApiResponse<Role>> {
    const { data } = await http.get(API.roles.item(id))
    return data
  },
  async create(body: Role): Promise<ApiResponse<Role>> {
    const { data } = await http.post(API.roles.list, body)
    return data
  },
  async update(id: number, body: Role): Promise<ApiResponse<Role>> {
    const { data } = await http.put(API.roles.item(id), body)
    return data
  },
  async remove(id: number) {
    return http.delete(API.roles.item(id))
  }
}
