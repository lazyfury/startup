import { http } from '../http'
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
    const { data } = await http.get(API.tenants.list, { params })
    return data
  },
  async get(id: number): Promise<ApiResponse<Tenant>> {
    const { data } = await http.get(API.tenants.item(id))
    return data
  },
  async create(body: Tenant): Promise<ApiResponse<Tenant>> {
    const { data } = await http.post(API.tenants.list, body)
    return data
  },
  async update(id: number, body: Tenant): Promise<ApiResponse<Tenant>> {
    const { data } = await http.put(API.tenants.item(id), body)
    return data
  },
  async remove(id: number) {
    return http.delete(API.tenants.item(id))
  }
}
