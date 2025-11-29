package io.sf.admin.api;

import io.sf.modules.auth.entity.User;
import io.sf.modules.auth.repository.UserRepository;
import io.sf.modules.acl.repository.UserRoleRepository;
import io.sf.modules.acl.entity.UserRole;
import io.sf.modules.acl.service.RoleAssignService;
import io.sf.modules.tenant.entity.Tenant;
import io.sf.modules.tenant.repository.TenantRepository;
import io.sf.modules.merchant.entity.Merchant;
import io.sf.modules.merchant.repository.MerchantRepository;
import io.sf.utils.crud.CrudApiController;
import io.sf.utils.crud.EnhancedDynamicDSL;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.sf.utils.response.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import java.util.List;

@RestController
@RequestMapping("user")
@Tag(name = "用户权限/用户管理", description = "用户管理接口（Admin）")
public class UserController extends CrudApiController<User, Long, UserRepository> {
    protected UserController(UserRepository repository) {
        super(repository);
    }

    @Override
    protected Class<User> entityClass() {
        return User.class;
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private RoleAssignService roleAssignService;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private MerchantRepository merchantRepository;

    @PostMapping("")
    @Operation(summary = "创建用户（自动加密密码）")
    public JsonResult<User> create(@NonNull @RequestBody User body) {
        if (body.getPassword() == null || body.getPassword().isBlank()) {
            return new JsonResult<User>(HttpStatus.BAD_REQUEST.value(), null, "password is required");
        }
        body.setPassword(passwordEncoder.encode(body.getPassword()));
        User saved = repository.save(body);
        return new JsonResult<User>(HttpStatus.OK, saved);
    }

    @Override
    @GetMapping("")
    @Operation(summary = "列表 + 动态查询（包含角色ID）")
    public JsonResult<Page<User>> list(@NonNull Pageable pageable, @RequestParam java.util.Map<String, String> params) {
        Specification<User> spec = new EnhancedDynamicDSL<>(User.class, params).build();
        Page<User> result = repository.findAll(spec, pageable);
        List<User> list = result.getContent();
        List<Long> userIds = list.stream().map(User::getId).filter(java.util.Objects::nonNull).toList();
        List<UserRole> links = userIds.isEmpty() ? java.util.Collections.emptyList() : userRoleRepository.findAllByUserIdIn(userIds);
        java.util.Map<Long, java.util.List<Long>> userRolesMap = new java.util.HashMap<>();
        for (UserRole ur : links) {
            userRolesMap.computeIfAbsent(ur.getUserId(), k -> new java.util.ArrayList<>()).add(ur.getRoleId());
        }
        java.util.List<Long> tenantIds = list.stream().map(User::getTenantId).filter(java.util.Objects::nonNull).distinct().toList();
        java.util.Map<Long, String> tenantNameMap = tenantIds.isEmpty() ? java.util.Collections.emptyMap() : tenantRepository.findAllById(tenantIds).stream().collect(java.util.stream.Collectors.toMap(Tenant::getId, Tenant::getName));
        java.util.List<Long> merchantIds = list.stream().map(User::getMerchantId).filter(java.util.Objects::nonNull).distinct().toList();
        java.util.Map<Long, String> merchantNameMap = merchantIds.isEmpty() ? java.util.Collections.emptyMap() : merchantRepository.findAllById(merchantIds).stream().collect(java.util.stream.Collectors.toMap(Merchant::getId, Merchant::getName));

        for (User u : list) {
            if (u.getId() == null) continue;
            u.setRoles(userRolesMap.getOrDefault(u.getId(), java.util.Collections.emptyList()));
            if (u.getTenantId() != null) u.setTenantName(tenantNameMap.get(u.getTenantId()));
            if (u.getMerchantId() != null) u.setMerchantName(merchantNameMap.get(u.getMerchantId()));
        }
        return new JsonResult<Page<User>>(HttpStatus.OK, result);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新用户（可同时提交角色ID，保留或加密密码）")
    public JsonResult<User> update(@NonNull @PathVariable Long id, @NonNull @RequestBody User body) {
        var existingOpt = repository.findById(id);
        if (existingOpt.isEmpty()) {
            return new JsonResult<User>(HttpStatus.NOT_FOUND, null);
        }
        User existing = existingOpt.get();
        if (body.getPassword() == null || body.getPassword().isBlank()) {
            body.setPassword(existing.getPassword());
        } else {
            String raw = body.getPassword();
            if (!(raw.startsWith("$2a$") || raw.startsWith("$2b$") || raw.startsWith("$2y$"))) {
                body.setPassword(passwordEncoder.encode(raw));
            }
        }
        body.setId(id);
        User saved = repository.save(body);
        List<Long> roleIds = body.getRoles();
        if (roleIds != null) {
            try {
                roleAssignService.replaceUserRoles(id, roleIds);
            } catch (Exception e) {
                return new JsonResult<User>(HttpStatus.BAD_REQUEST.value(), null, e.getMessage());
            }
        }
        return new JsonResult<User>(HttpStatus.OK, saved);
    }
}
