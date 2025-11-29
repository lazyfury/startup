import { request } from '../http'
import { API } from '../endpoints'
import type { ApiResponse, Page } from '../types'

export type Tenant = {
  id?: number
  name: string
  code: string
  description?: string
  status?: boolean
  createdAt?: string
  updatedAt?: string
}

export const TenantApi = {
  async list(params: { page: number; size: number }): Promise<ApiResponse<Page<Tenant>>> {
    const qp = `?page=${params.page}&size=${params.size}`
    const res = await request(`${API.tenants.list}${qp}`)
    return res.json()
  },
  async get(id: number): Promise<ApiResponse<Tenant>> {
    const res = await request(API.tenants.item(id))
    return res.json()
  },
  async create(body: Tenant): Promise<ApiResponse<Tenant>> {
    const res = await request(API.tenants.list, { method: 'POST', body: JSON.stringify(body) })
    return res.json()
  },
  async update(id: number, body: Tenant): Promise<ApiResponse<Tenant>> {
    const res = await request(API.tenants.item(id), { method: 'PUT', body: JSON.stringify(body) })
    return res.json()
  },
  async remove(id: number) {
    return request(API.tenants.item(id), { method: 'DELETE' })
  }
}

