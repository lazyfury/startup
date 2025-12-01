import { http } from '../http'
import { API } from '../endpoints'
import type { ApiResponse } from '../types'
import type { ConfigSetting } from '../types'

export const ConfigApi = {
  async listByGroup(groupId: number): Promise<ApiResponse<ConfigSetting[]>> {
    const { data } = await http.get(API.config.group(groupId))
    return data
  },
  async saveByGroup(groupId: number, settings: ConfigSetting[]): Promise<ApiResponse<ConfigSetting[]>> {
    const { data } = await http.post(API.config.group(groupId), settings)
    return data
  },
  async deactivateGroup(groupId: number) {
    return http.post(API.config.deactivateGroup(groupId))
  }
}
