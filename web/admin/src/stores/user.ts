import { defineStore } from 'pinia'
import { UserApi } from '../api/apis/user'

type UserInfo = {
  id: number
  username: string
  enabled?: boolean
  tenantId?: number | null
  avatarUrl?: string
}

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') as string | null,
    user: null as UserInfo | null
  }),
  actions: {
    setToken(token: string | null) {
      this.token = token
      if (token) localStorage.setItem('token', token)
      else localStorage.removeItem('token')
    },
    async loadProfile() {
      if (!this.token) {
        this.user = null
        return false
      }
      try {
        const json = await UserApi.profile()
        if (!(json.code >= 200 && json.code < 300)) {
          this.setToken(null)
          this.user = null
          return false
        }
        const u = json.data as UserInfo
        u.avatarUrl = `https://api.dicebear.com/7.x/initials/svg?seed=${encodeURIComponent(u.username || 'U')}`
        this.user = u
        return true
      } catch {
        this.setToken(null)
        this.user = null
        return false
      }
    },
    logout() {
      this.setToken(null)
      this.user = null
    }
  }
})
