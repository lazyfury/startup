package io.sf.admin.api;

import io.sf.modules.acl.entity.RolePermission;
import io.sf.modules.acl.entity.UserRole;
import io.sf.modules.acl.repository.RolePermissionRepository;
import io.sf.modules.acl.repository.UserRoleRepository;
import io.sf.utils.response.JsonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("acl")
@Tag(name = "角色权限绑定", description = "角色与权限、用户与角色绑定接口")
public class RoleAssignController {

    private final RolePermissionRepository rolePermissionRepository;
    private final UserRoleRepository userRoleRepository;

    public RoleAssignController(RolePermissionRepository rolePermissionRepository, UserRoleRepository userRoleRepository) {
        this.rolePermissionRepository = rolePermissionRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @GetMapping("/role/{roleId}/permissions")
    @Operation(summary = "查询角色的权限")
    public JsonResult<List<RolePermission>> rolePermissions(@PathVariable Long roleId) {
        return new JsonResult<>(HttpStatus.OK, rolePermissionRepository.findAllByRoleId(roleId));
    }

    @PostMapping("/role/{roleId}/permissions")
    @Operation(summary = "替换角色的权限")
    public JsonResult<Void> replaceRolePermissions(@PathVariable Long roleId, @RequestBody List<Long> permissionIds) {
        rolePermissionRepository.deleteAllByRoleId(roleId);
        for (Long pid : permissionIds) {
            RolePermission rp = new RolePermission();
            rp.setRoleId(roleId);
            rp.setPermissionId(pid);
            rolePermissionRepository.save(rp);
        }
        return new JsonResult<>(HttpStatus.NO_CONTENT, null);
    }

    @GetMapping("/user/{userId}/roles")
    @Operation(summary = "查询用户的角色")
    public JsonResult<List<UserRole>> userRoles(@PathVariable Long userId) {
        return new JsonResult<>(HttpStatus.OK, userRoleRepository.findAllByUserId(userId));
    }

    @PostMapping("/user/{userId}/roles")
    @Operation(summary = "替换用户的角色")
    public JsonResult<Void> replaceUserRoles(@PathVariable Long userId, @RequestBody List<Long> roleIds) {
        userRoleRepository.deleteAllByUserId(userId);
        for (Long rid : roleIds) {
            UserRole ur = new UserRole();
            ur.setUserId(userId);
            ur.setRoleId(rid);
            userRoleRepository.save(ur);
        }
        return new JsonResult<>(HttpStatus.NO_CONTENT, null);
    }
}

