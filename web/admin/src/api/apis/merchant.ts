import { request } from '../http'
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
    const qp = `?page=${params.page}&size=${params.size}`
    const res = await request(`${API.merchants.list}${qp}`)
    return res.json()
  },
  async get(id: number): Promise<ApiResponse<Merchant>> {
    const res = await request(API.merchants.item(id))
    return res.json()
  },
  async create(body: Merchant): Promise<ApiResponse<Merchant>> {
    const res = await request(API.merchants.list, { method: 'POST', body: JSON.stringify(body) })
    return res.json()
  },
  async update(id: number, body: Merchant): Promise<ApiResponse<Merchant>> {
    const res = await request(API.merchants.item(id), { method: 'PUT', body: JSON.stringify(body) })
    return res.json()
  },
  async remove(id: number) {
    return request(API.merchants.item(id), { method: 'DELETE' })
  }
}

