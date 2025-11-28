package io.sf.modules.acl.service;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class RoleAssignService {
    private final RolePermissionRepository rolePermissionRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;

    public RoleAssignService(RolePermissionRepository rolePermissionRepository, UserRoleRepository userRoleRepository, RoleRepository roleRepository, PermissionRepository permissionRepository, UserRepository userRepository) {
        this.rolePermissionRepository = rolePermissionRepository;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public boolean replaceRolePermissions(Long roleId, List<Long> permissionIds) {
        var roleOpt = roleRepository.findById(roleId);
        if (roleOpt.isEmpty()) return false;
        Role role = roleOpt.get();
        var distinctPermissionIds = permissionIds.stream().filter(Objects::nonNull).distinct().toList();
        for (Long pid : distinctPermissionIds) {
            var permOpt = permissionRepository.findById(pid);
            if (permOpt.isEmpty()) return false;
            Permission p = permOpt.get();
            boolean match = p.getScopeType() == role.getScopeType() && Objects.equals(p.getScopeId(), role.getScopeId());
            if (!match) return false;
        }
        var existingLinks = rolePermissionRepository.findAllByRoleId(roleId);
        var existingIds = existingLinks.stream().map(RolePermission::getPermissionId).distinct().toList();
        var toDelete = existingIds.stream().filter(id -> !distinctPermissionIds.contains(id)).toList();
        var toAdd = distinctPermissionIds.stream().filter(id -> !existingIds.contains(id)).toList();
        for (Long pid : toDelete) {
            rolePermissionRepository.deleteByRoleIdAndPermissionId(roleId, pid);
        }
        var newLinks = toAdd.stream().map(pid -> {
            RolePermission rp = new RolePermission();
            rp.setRoleId(roleId);
            rp.setPermissionId(pid);
            return rp;
        }).toList();
        if (!newLinks.isEmpty()) {
            rolePermissionRepository.saveAll(newLinks);
        }
        return true;
    }

    @Transactional
    public boolean replaceUserRoles(Long userId, List<Long> roleIds) {
        var userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) return false;
        User user = userOpt.get();
        var distinctRoleIds = roleIds.stream().filter(Objects::nonNull).distinct().toList();
        for (Long rid : distinctRoleIds) {
            var roleOpt = roleRepository.findById(rid);
            if (roleOpt.isEmpty()) return false;
            Role r = roleOpt.get();
            boolean allowed = (r.getScopeType() == ScopeType.TENANT && Objects.equals(r.getScopeId(), user.getTenantId()))
                    || (r.getScopeType() == ScopeType.MERCHANT && Objects.equals(r.getScopeId(), user.getMerchantId()));
            if (!allowed) return false;
        }
        var existingLinks = userRoleRepository.findAllByUserId(userId);
        var existingIds = existingLinks.stream().map(UserRole::getRoleId).distinct().toList();
        var toDelete = existingIds.stream().filter(id -> !distinctRoleIds.contains(id)).toList();
        var toAdd = distinctRoleIds.stream().filter(id -> !existingIds.contains(id)).toList();
        for (Long rid : toDelete) {
            userRoleRepository.deleteByUserIdAndRoleId(userId, rid);
        }
        var newLinks = toAdd.stream().map(rid -> {
            UserRole ur = new UserRole();
            ur.setUserId(userId);
            ur.setRoleId(rid);
            return ur;
        }).toList();
        if (!newLinks.isEmpty()) {
            userRoleRepository.saveAll(newLinks);
        }
        return true;
    }
}
