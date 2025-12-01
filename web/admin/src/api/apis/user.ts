import { http } from '../http'
import { API } from '../endpoints'
import type { ApiResponse, UserProfile } from '../types'

export const UserApi = {
  async login(payload: { username: string; password: string }): Promise<ApiResponse<{ token: string }>> {
    const { data } = await http.post(API.auth.login, payload)
    return data
  },
  async profile(): Promise<ApiResponse<UserProfile>> {
    const { data } = await http.get(API.auth.profile)
    return data
  },
  async createAdminDebug(): Promise<ApiResponse<any>> {
    const { data } = await http.post(API.auth.createAdminDebug)
    return data
  }
}
