import { http } from '../http'
import { API } from '../endpoints'
import type { ApiResponse, Page } from '../types'

export type Merchant = {
  id?: number
  name: string
  code: string
  tenantId?: number | null
  status?: boolean
  createdAt?: string
  updatedAt?: string
}

export const MerchantApi = {
  async list(params: { page: number; size: number }): Promise<ApiResponse<Page<Merchant>>> {
    const { data } = await http.get(API.merchants.list, { params })
    return data
  },
  async get(id: number): Promise<ApiResponse<Merchant>> {
    const { data } = await http.get(API.merchants.item(id))
    return data
  },
  async create(body: Merchant): Promise<ApiResponse<Merchant>> {
    const { data } = await http.post(API.merchants.list, body)
    return data
  },
  async update(id: number, body: Merchant): Promise<ApiResponse<Merchant>> {
    const { data } = await http.put(API.merchants.item(id), body)
    return data
  },
  async remove(id: number) {
    return http.delete(API.merchants.item(id))
  }
}
