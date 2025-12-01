import { http } from '../http'
import { API } from '../endpoints'
import type { ApiResponse, Page, AdminUser } from '../types'

export const AdminUserApi = {
  async list(params: { page: number; size: number }): Promise<ApiResponse<Page<AdminUser>>> {
    const { data } = await http.get(API.users.list, { params: { page: params.page, size: params.size } })
    return data
  },
  async get(id: number): Promise<ApiResponse<AdminUser>> {
    const { data } = await http.get(API.users.item(id))
    return data
  },
  async create(body: AdminUser): Promise<ApiResponse<AdminUser>> {
    const { data } = await http.post(API.users.list, body)
    return data
  },
  async update(id: number, body: AdminUser): Promise<ApiResponse<AdminUser>> {
    const { data } = await http.put(API.users.item(id), body)
    return data
  },
  async remove(id: number) {
    return http.delete(API.users.item(id))
  }
}
