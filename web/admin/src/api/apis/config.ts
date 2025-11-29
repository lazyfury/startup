import { request } from '../http'
import { API } from '../endpoints'
import type { ApiResponse } from '../types'
import type { ConfigSetting } from '../types'

export const ConfigApi = {
  async listByGroup(groupId: number): Promise<ApiResponse<ConfigSetting[]>> {
    const res = await request(API.config.group(groupId))
    return res.json()
  },
  async saveByGroup(groupId: number, settings: ConfigSetting[]): Promise<ApiResponse<ConfigSetting[]>> {
    const res = await request(API.config.group(groupId), { method: 'POST', body: JSON.stringify(settings) })
    return res.json()
  },
  async deactivateGroup(groupId: number) {
    return request(API.config.deactivateGroup(groupId), { method: 'POST' })
  }
}
