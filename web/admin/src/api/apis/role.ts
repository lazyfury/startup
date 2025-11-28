import { request } from '../http'
import { API } from '../endpoints'
import type { ApiResponse, Page, Role } from '../types'

export const RoleApi = {
  async list(params: { page: number; size: number }): Promise<ApiResponse<Page<Role>>> {
    const qp = `?page=${params.page}&size=${params.size}`
    const res = await request(`${API.roles.list}${qp}`)
    return res.json()
  },
  async get(id: number): Promise<ApiResponse<Role>> {
    const res = await request(API.roles.item(id))
    return res.json()
  },
  async create(body: Role): Promise<ApiResponse<Role>> {
    const res = await request(API.roles.list, { method: 'POST', body: JSON.stringify(body) })
    return res.json()
  },
  async update(id: number, body: Role): Promise<ApiResponse<Role>> {
    const res = await request(API.roles.item(id), { method: 'PUT', body: JSON.stringify(body) })
    return res.json()
  },
  async remove(id: number): Promise<Response> {
    return request(API.roles.item(id), { method: 'DELETE' })
  }
}
