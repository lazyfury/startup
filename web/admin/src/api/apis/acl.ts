import { request } from '../http'
import { API } from '../endpoints'
import type { ApiResponse, HttpResponseLike } from '../types'

export const AclApi = {
  async getRolePermissions(roleId: number): Promise<ApiResponse<Array<{ roleId: number; permissionId: number }>>> {
    const res = await request(API.acl.rolePermissions(roleId))
    return res.json()
  },
  async replaceRolePermissions(roleId: number, permissionIds: number[]): Promise<HttpResponseLike> {
    return request(API.acl.rolePermissions(roleId), {
      method: 'POST',
      body: JSON.stringify(permissionIds)
    })
  }
}
