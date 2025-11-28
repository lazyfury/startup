export type ApiResponse<T = unknown> = {
  code: number
  message?: string
  data: T
}

export type Page<T> = {
  content: T[]
  totalElements: number
  totalPages?: number
  size?: number
  number?: number
}

export const SCOPE_TYPES = ['SYSTEM', 'TENANT', 'USER', 'APP', 'MERCHANT'] as const
export type ScopeType = typeof SCOPE_TYPES[number]

export type UserProfile = {
  id: number
  username: string
  enabled?: boolean
  tenantId?: number | null
  avatarUrl?: string
}

export type ConfigGroup = {
  id?: number
  name: string
  code: string
  description?: string
  scopeType: ScopeType
  scopeId?: number | null
  status?: boolean
  createdAt?: string
  updatedAt?: string
}

export type MenuItem = {
  id?: number
  parentId?: number | null
  name: string
  code: string
  path?: string
  component?: string
  redirect?: string
  icon?: string
  type?: string
  orderNo?: number
  hidden?: boolean
  status?: boolean
  createdAt?: string
  updatedAt?: string
  children?: MenuItem[]
}

export type AdminUser = {
  id?: number
  username: string
  password?: string
  enabled?: boolean
  tenantId?: number | null
  roles?: number[]
  createdAt?: string
  updatedAt?: string
}

export type Role = {
  id?: number
  name: string
  code: string
  scopeType?: ScopeType
  scopeId?: number | null
  status?: boolean
  createdAt?: string
  updatedAt?: string
}

export type Permission = {
  id?: number
  name: string
  code: string
  description?: string
  tag?: string
  parentId?: number | null
  children?: Permission[]
  scopeType?: ScopeType
  scopeId?: number | null
  status?: boolean
  createdAt?: string
  updatedAt?: string
}
