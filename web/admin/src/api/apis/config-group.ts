import { http } from '../http'
import { API } from '../endpoints'
import type { ApiResponse, Page, ConfigGroup } from '../types'

export const ConfigGroupApi = {
  async list(params: { page: number; size: number }): Promise<ApiResponse<Page<ConfigGroup>>> {
    const { data } = await http.get(API.configGroup.list, { params })
    return data
  },
  async get(id: number): Promise<ApiResponse<ConfigGroup>> {
    const { data } = await http.get(API.configGroup.item(id))
    return data
  },
  async create(body: ConfigGroup): Promise<ApiResponse<ConfigGroup>> {
    const { data } = await http.post(API.configGroup.list, body)
    return data
  },
  async update(id: number, body: ConfigGroup): Promise<ApiResponse<ConfigGroup>> {
    const { data } = await http.put(API.configGroup.item(id), body)
    return data
  },
  async remove(id: number) {
    return http.delete(API.configGroup.item(id))
  }
}
