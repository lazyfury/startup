package io.sf.admin.api;

import io.sf.modules.acl.service.RoleAssignService;
import io.sf.modules.acl.entity.RolePermission;
import io.sf.modules.acl.entity.UserRole;
import io.sf.modules.acl.repository.RolePermissionRepository;
import io.sf.modules.acl.repository.UserRoleRepository;
import io.sf.modules.acl.entity.Role;
import io.sf.modules.acl.entity.Permission;
import io.sf.modules.acl.repository.RoleRepository;
import io.sf.modules.acl.repository.PermissionRepository;
import io.sf.modules.auth.entity.User;
import io.sf.modules.auth.repository.UserRepository;
import io.sf.modules.config.entity.ScopeType;
import io.sf.utils.response.JsonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("acl")
@Tag(name = "用户权限/角色权限绑定", description = "角色与权限、用户与角色绑定接口")
public class RoleAssignController {

    private final RolePermissionRepository rolePermissionRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final RoleAssignService roleAssignService;

    public RoleAssignController(RolePermissionRepository rolePermissionRepository, UserRoleRepository userRoleRepository, RoleRepository roleRepository, PermissionRepository permissionRepository, UserRepository userRepository, RoleAssignService roleAssignService) {
        this.rolePermissionRepository = rolePermissionRepository;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.userRepository = userRepository;
        this.roleAssignService = roleAssignService;
    }

    @GetMapping("/role/{roleId}/permissions")
    @Operation(summary = "查询角色的权限")
    public JsonResult<List<RolePermission>> rolePermissions(@NonNull @PathVariable Long roleId) {
        var roleOpt = roleRepository.findById(roleId);
        if (roleOpt.isEmpty()) return new JsonResult<>(HttpStatus.NOT_FOUND, null);
        Role role = roleOpt.get();
        List<RolePermission> all = rolePermissionRepository.findAllByRoleId(roleId);
        List<RolePermission> filtered = new java.util.ArrayList<>();
        for (RolePermission rp : all) {
            var permissionId = Objects.requireNonNull(rp.getPermissionId());
            var permOpt = permissionRepository.findById(permissionId);
            if (permOpt.isPresent()) {
                Permission p = permOpt.get();
                boolean match = p.getScopeType() == role.getScopeType() && Objects.equals(p.getScopeId(), role.getScopeId());
                if (match) filtered.add(rp);
            }
        }
        return new JsonResult<>(HttpStatus.OK, filtered);
    }

    @PostMapping("/role/{roleId}/permissions")
    @Operation(summary = "替换角色的权限")
    public JsonResult<Void> replaceRolePermissions(@NonNull @PathVariable Long roleId, @RequestBody List<Long> permissionIds) {
        var roleOpt = roleRepository.findById(roleId);
        if (roleOpt.isEmpty()) return new JsonResult<>(HttpStatus.NOT_FOUND, null);
        try {
            roleAssignService.replaceRolePermissions(roleId, permissionIds);
            return new JsonResult<>(HttpStatus.NO_CONTENT, null);
        } catch (Exception e) {
            return new JsonResult<>(HttpStatus.BAD_REQUEST.value(), null, e.getMessage());
        }
    }

    @GetMapping("/user/{userId}/roles")
    @Operation(summary = "查询用户的角色")
    public JsonResult<List<UserRole>> userRoles(@NonNull @PathVariable Long userId) {
        var userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) return new JsonResult<>(HttpStatus.NOT_FOUND, null);
        User user = userOpt.get();
        List<UserRole> all = userRoleRepository.findAllByUserId(userId);
        List<UserRole> filtered = new java.util.ArrayList<>();
        for (UserRole ur : all) {
            var roleId = Objects.requireNonNull(ur.getRoleId());
            var roleOpt = roleRepository.findById(roleId);
            if (roleOpt.isPresent()) {
                Role r = roleOpt.get();
                boolean allowed = (r.getScopeType() == ScopeType.SYSTEM && Boolean.TRUE.equals(user.getIsStaff()))
                        || (r.getScopeType() == ScopeType.TENANT && Objects.equals(r.getScopeId(), user.getTenantId()))
                        || (r.getScopeType() == ScopeType.MERCHANT && Objects.equals(r.getScopeId(), user.getMerchantId()));
                if (allowed) filtered.add(ur);
            }
        }
        return new JsonResult<>(HttpStatus.OK, filtered);
    }

    @PostMapping("/user/{userId}/roles")
    @Operation(summary = "替换用户的角色")
    public JsonResult<Void> replaceUserRoles(@NonNull @PathVariable Long userId, @RequestBody List<Long> roleIds) {
        var userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) return new JsonResult<>(HttpStatus.NOT_FOUND, null);
        try {
            roleAssignService.replaceUserRoles(userId, roleIds);
            return new JsonResult<>(HttpStatus.NO_CONTENT, null);
        } catch (Exception e) {
            return new JsonResult<>(HttpStatus.BAD_REQUEST.value(), null, e.getMessage());
        }
    }
}
