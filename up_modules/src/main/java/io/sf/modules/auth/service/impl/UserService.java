package io.sf.modules.auth.service.impl;

import io.sf.modules.auth.entity.User;
import io.sf.modules.auth.repository.UserRepository;
import io.sf.modules.auth.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import io.sf.modules.acl.entity.Role;
import io.sf.modules.acl.entity.RolePermission;
import io.sf.modules.acl.entity.UserRole;
import io.sf.modules.acl.entity.Permission;
import io.sf.modules.acl.repository.RoleRepository;
import io.sf.modules.acl.repository.RolePermissionRepository;
import io.sf.modules.acl.repository.UserRoleRepository;
import io.sf.modules.acl.repository.PermissionRepository;
import io.sf.modules.config.entity.ScopeType;
import io.sf.modules.tenant.repository.TenantRepository;
import io.sf.modules.merchant.repository.MerchantRepository;
import io.sf.modules.acl.service.RoleAssignService;

import java.util.*;
import java.util.Optional;

@Slf4j
@Service
public class UserService implements IUserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private RoleAssignService roleAssignService;

    @Override
    public Optional<User> getUserByUsername(String username) {
        return Optional.ofNullable(userRepository.findByUsername(username));
    }

    @Override
    public  Optional<User> findUserById(Long id){
        Objects.requireNonNull(id);
        return userRepository.findById(id);
    }

    @Override
    public int registerUser(User user) {
        // 对明文密码进行加盐哈希存储（BCrypt 内置随机盐）
        String encoded = passwordEncoder.encode(user.getPassword());
        user.setPassword(encoded);
        try {
            userRepository.save(user);
            return 1;
        } 
        catch(DataIntegrityViolationException e){
            throw new RuntimeException("用户名已存在", e);
        }
        catch (Exception e) {
            log.info("注册用户异常=======",e);
            throw new RuntimeException("注册用户失败", e);
        }
    }

    // login 
    @Override
    public Optional<User> loginUser(String username, String password) {
        Optional<User> user = Optional.ofNullable(userRepository.findByUsername(username));
        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            return user;
        }
        return Optional.empty();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(User user) {
        List<UserRole> urs = userRoleRepository.findAllByUserId(user.getId());
        Set<Long> roleIds = new HashSet<>();
        for (UserRole ur : urs) roleIds.add(ur.getRoleId());
        List<Role> roles = roleIds.isEmpty() ? Collections.emptyList() : roleRepository.findAllById(roleIds);
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role r : roles) {
            boolean allowed = (r.getScopeType() == ScopeType.SYSTEM && Boolean.TRUE.equals(user.getIsStaff()))
                    || (r.getScopeType() == ScopeType.TENANT && Objects.equals(r.getScopeId(), user.getTenantId()))
                    || (r.getScopeType() == ScopeType.MERCHANT && Objects.equals(r.getScopeId(), user.getMerchantId()));
            if (allowed) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + r.getCode().toUpperCase()));
            }
        }
        Set<Long> permIds = new HashSet<>();
        for (Role r : roles) {
            List<RolePermission> rps = rolePermissionRepository.findAllByRoleId(r.getId());
            for (RolePermission rp : rps) permIds.add(rp.getPermissionId());
        }
        List<Permission> perms = permIds.isEmpty() ? Collections.emptyList() : permissionRepository.findAllById(permIds);
        for (Permission p : perms) {
            authorities.add(new SimpleGrantedAuthority("PERM_" + p.getCode().toUpperCase()));
        }
        return authorities;
    }

    @Override
    public List<User> enrichUsers(List<User> users) {
        if (users == null || users.isEmpty()) return users;
        List<Long> userIds = users.stream().map(User::getId).filter(Objects::nonNull).toList();
        List<UserRole> links = userIds.isEmpty() ? Collections.emptyList() : userRoleRepository.findAllByUserIdIn(userIds);
        Map<Long, List<Long>> userRolesMap = new HashMap<>();
        for (UserRole ur : links) {
            userRolesMap.computeIfAbsent(ur.getUserId(), k -> new ArrayList<>()).add(ur.getRoleId());
        }
        List<Long> tenantIds = users.stream().map(User::getTenantId).filter(Objects::nonNull).distinct().toList();
        Map<Long, String> tenantNameMap = tenantIds.isEmpty() ? Collections.emptyMap() : tenantRepository.findAllById(tenantIds).stream().collect(java.util.stream.Collectors.toMap(io.sf.modules.tenant.entity.Tenant::getId, io.sf.modules.tenant.entity.Tenant::getName));
        List<Long> merchantIds = users.stream().map(User::getMerchantId).filter(Objects::nonNull).distinct().toList();
        Map<Long, String> merchantNameMap = merchantIds.isEmpty() ? Collections.emptyMap() : merchantRepository.findAllById(merchantIds).stream().collect(java.util.stream.Collectors.toMap(io.sf.modules.merchant.entity.Merchant::getId, io.sf.modules.merchant.entity.Merchant::getName));

        for (User u : users) {
            if (u.getId() == null) continue;
            u.setRoles(userRolesMap.getOrDefault(u.getId(), Collections.emptyList()));
            if (u.getTenantId() != null) u.setTenantName(tenantNameMap.get(u.getTenantId()));
            if (u.getMerchantId() != null) u.setMerchantName(merchantNameMap.get(u.getMerchantId()));
        }
        return users;
    }

    @Override
    public User enrichUser(User u) {
        if (u == null || u.getId() == null) return u;
        List<UserRole> links = userRoleRepository.findAllByUserId(u.getId());
        u.setRoles(links.stream().map(UserRole::getRoleId).toList());
        Long tid = u.getTenantId();
        if (tid != null) {
            tenantRepository.findById(tid).ifPresent(tt -> u.setTenantName(tt.getName()));
        }
        Long mid = u.getMerchantId();
        if (mid != null) {
            merchantRepository.findById(mid).ifPresent(mm -> u.setMerchantName(mm.getName()));
        }
        return u;
    }

    @Override
    public User updateUser(Long id, User body, List<Long> roleIds) {
        Objects.requireNonNull(id);
        User existing = userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("user not found"));
        if (body.getPassword() == null || body.getPassword().isBlank()) {
            body.setPassword(existing.getPassword());
        } else {
            String raw = body.getPassword();
            if (!(raw.startsWith("$2a$") || raw.startsWith("$2b$") || raw.startsWith("$2y$"))) {
                body.setPassword(passwordEncoder.encode(raw));
            }
        }
        body.setId(id);
        User saved = userRepository.save(body);
        if (roleIds != null) {
            roleAssignService.replaceUserRoles(id, roleIds);
        }
        return saved;
    }

    @Override
    public void deleteUser(Long id) {
        Objects.requireNonNull(id, "user id is required");
        if (!userRepository.existsById(id)) return;
        userRepository.deleteById(id);
    }
}
