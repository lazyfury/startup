import { request } from '../http'
import { API } from '../endpoints'
import type { ApiResponse, Page, ConfigGroup, HttpResponseLike } from '../types'

export const ConfigGroupApi = {
  async list(params: { page: number; size: number }): Promise<ApiResponse<Page<ConfigGroup>>> {
    const qp = `?page=${params.page}&size=${params.size}`
    const res = await request(`${API.configGroup.list}${qp}`)
    return res.json()
  },
  async get(id: number): Promise<ApiResponse<ConfigGroup>> {
    const res = await request(API.configGroup.item(id))
    return res.json()
  },
  async create(body: ConfigGroup): Promise<ApiResponse<ConfigGroup>> {
    const res = await request(API.configGroup.list, {
      method: 'POST',
      body: JSON.stringify(body)
    })
    return res.json()
  },
  async update(id: number, body: ConfigGroup): Promise<ApiResponse<ConfigGroup>> {
    const res = await request(API.configGroup.item(id), {
      method: 'PUT',
      body: JSON.stringify(body)
    })
    return res.json()
  },
  async remove(id: number): Promise<HttpResponseLike> {
    return request(API.configGroup.item(id), { method: 'DELETE' })
  }
}
