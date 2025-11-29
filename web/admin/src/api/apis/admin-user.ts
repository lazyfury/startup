import { request } from '../http'
import { API } from '../endpoints'
import type { ApiResponse, Page, AdminUser, HttpResponseLike } from '../types'

export const AdminUserApi = {
  async list(params: { page: number; size: number }): Promise<ApiResponse<Page<AdminUser>>> {
    const qp = `?page=${params.page}&size=${params.size}`
    const res = await request(`${API.users.list}${qp}`)
    return res.json()
  },
  async get(id: number): Promise<ApiResponse<AdminUser>> {
    const res = await request(API.users.item(id))
    return res.json()
  },
  async create(body: AdminUser): Promise<ApiResponse<AdminUser>> {
    const res = await request(API.users.list, { method: 'POST', body: JSON.stringify(body) })
    return res.json()
  },
  async update(id: number, body: AdminUser): Promise<ApiResponse<AdminUser>> {
    const res = await request(API.users.item(id), { method: 'PUT', body: JSON.stringify(body) })
    return res.json()
  },
  async remove(id: number): Promise<HttpResponseLike> {
    return request(API.users.item(id), { method: 'DELETE' })
  }
}
