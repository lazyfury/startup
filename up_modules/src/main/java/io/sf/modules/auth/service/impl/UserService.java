package io.sf.modules.auth.service.impl;

import io.sf.modules.auth.entity.User;
import io.sf.modules.auth.mapper.UserMapper;
import io.sf.modules.auth.repository.UserRepository;
import io.sf.modules.auth.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
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

import java.util.*;
import java.util.Optional;

@Slf4j
@Service
public class UserService implements IUserService {
    @Autowired
    private UserMapper userMapper;

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

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userMapper.getUserByUsername(username);
    }

    @Override
    public  Optional<User> findUserById(Long id){
        return userRepository.findById(id);
    }

    @Override
    public int registerUser(User user) {
        // 对明文密码进行加盐哈希存储（BCrypt 内置随机盐）
        String encoded = passwordEncoder.encode(user.getPassword());
        user.setPassword(encoded);
        try {
            return userMapper.insertUser(user);
        } 
        catch(DuplicateKeyException e){
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
        Optional<User> user = userMapper.getUserByUsername(username);
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
            if (r.getScopeType() == ScopeType.SYSTEM
                    || (r.getScopeType() == ScopeType.TENANT && Objects.equals(r.getScopeId(), user.getTenantId()))
                    || (r.getScopeType() == ScopeType.MERCHANT && Objects.equals(r.getScopeId(), user.getMerchantId()))
            ) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + r.getCode()));
            }
        }
        Set<Long> permIds = new HashSet<>();
        for (Role r : roles) {
            List<RolePermission> rps = rolePermissionRepository.findAllByRoleId(r.getId());
            for (RolePermission rp : rps) permIds.add(rp.getPermissionId());
        }
        List<Permission> perms = permIds.isEmpty() ? Collections.emptyList() : permissionRepository.findAllById(permIds);
        for (Permission p : perms) {
            authorities.add(new SimpleGrantedAuthority("PERM_" + p.getCode()));
        }
        return authorities;
    }
}
