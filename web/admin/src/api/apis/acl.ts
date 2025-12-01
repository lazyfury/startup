import { http } from '../http'
import { API } from '../endpoints'
import type { ApiResponse, HttpResponseLike } from '../types'

export const AclApi = {
  async getRolePermissions(roleId: number): Promise<ApiResponse<Array<{ roleId: number; permissionId: number }>>> {
    const { data } = await http.get(API.acl.rolePermissions(roleId))
    return data
  },
  async replaceRolePermissions(roleId: number, permissionIds: number[]): Promise<HttpResponseLike> {
    return http.post(API.acl.rolePermissions(roleId), permissionIds)
  },
  async getUserRoles(userId: number): Promise<ApiResponse<Array<{ userId: number; roleId: number }>>> {
    const { data } = await http.get(API.acl.userRoles(userId))
    return data
  },
  async replaceUserRoles(userId: number, roleIds: number[]): Promise<HttpResponseLike> {
    return http.post(API.acl.userRoles(userId), roleIds)
  }
}
