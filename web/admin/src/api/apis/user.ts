import { request } from '../http'
import { API } from '../endpoints'
import type { ApiResponse, UserProfile } from '../types'

export const UserApi = {
  async login(payload: { username: string; password: string }): Promise<ApiResponse<{ token: string }>> {
    const res = await request(API.auth.login, {
      method: 'POST',
      body: JSON.stringify(payload)
    })
    return res.json()
  },
  async profile(): Promise<ApiResponse<UserProfile>> {
    const res = await request(API.auth.profile)
    return res.json()
  },
  async createAdminDebug(): Promise<ApiResponse<any>> {
    const res = await request(API.auth.createAdminDebug, { method: 'POST' })
    return res.json()
  }
}
