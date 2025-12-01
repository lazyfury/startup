package io.sf.admin.api;

import io.sf.admin.api.dto.UserUpdateDto;
import io.sf.modules.auth.entity.User;
import io.sf.admin.api.dto.UserCreateDto;
import io.sf.modules.auth.repository.UserRepository;
import io.sf.modules.auth.service.impl.UserService;
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
import java.util.NoSuchElementException;

@RestController
@RequestMapping("user")
@Tag(name = "用户权限/用户管理", description = "用户管理接口（Admin）")
public class UserController {
    private final UserRepository repository;

    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Autowired
    private UserService userService;

    @PostMapping("")
    @Operation(summary = "创建用户（自动加密密码）")
    public JsonResult<Integer> create(@NonNull @RequestBody UserCreateDto body) {
        if (body.getPassword() == null || body.getPassword().isBlank()) {
            return new JsonResult<Integer>(HttpStatus.BAD_REQUEST.value(), 0, "password is required");
        }
        User entity = new User();
        entity.setUsername(body.getUsername());
        entity.setPassword(passwordEncoder.encode(body.getPassword()));
        entity.setEnabled(body.getEnabled() == null ? Boolean.TRUE : body.getEnabled());
        entity.setIsStaff(body.getIsStaff() == null ? Boolean.FALSE : body.getIsStaff());
        entity.setTenantId(body.getTenantId());
        entity.setMerchantId(body.getMerchantId());
        Integer saved = userService.registerUser(entity);
        return new JsonResult<Integer>(HttpStatus.OK, saved);
    }

    @GetMapping("")
    @Operation(summary = "列表 + 动态查询（包含角色ID）")
    public JsonResult<Page<User>> list(@NonNull Pageable pageable, @RequestParam java.util.Map<String, String> params) {
        Specification<User> spec = new EnhancedDynamicDSL<>(User.class, params).build();
        Page<User> result = repository.findAll(spec, pageable);
        userService.enrichUsers(result.getContent());
        return new JsonResult<Page<User>>(HttpStatus.OK, result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询单条（包含角色ID与名称）")
    public JsonResult<User> getById(@NonNull @PathVariable Long id) {
        var existingOpt = repository.findById(id);
        if (existingOpt.isEmpty()) {
            return new JsonResult<User>(HttpStatus.NOT_FOUND, null);
        }
        User u = existingOpt.get();
        return new JsonResult<User>(HttpStatus.OK, userService.enrichUser(u));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新用户（可同时提交角色ID，保留或加密密码）")
    public JsonResult<User> update(@NonNull @PathVariable Long id, @NonNull @RequestBody UserUpdateDto body) {
        try {
            User entity = new User();
            entity.setUsername(body.getUsername());
            entity.setPassword(body.getPassword() == null || body.getPassword().isBlank() ? null : passwordEncoder.encode(body.getPassword()));
            entity.setEnabled(body.getEnabled());
            entity.setIsStaff(body.getIsStaff());
            entity.setTenantId(body.getTenantId());
            entity.setMerchantId(body.getMerchantId());
            User saved = userService.updateUser(id, entity, body.getRoles());
            return new JsonResult<User>(HttpStatus.OK, saved);
        } catch (NoSuchElementException e) {
            return new JsonResult<User>(HttpStatus.NOT_FOUND, null);
        } catch (Exception e) {
            return new JsonResult<User>(HttpStatus.BAD_REQUEST.value(), null, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户")
    public JsonResult<Void> delete(@NonNull @PathVariable Long id) {
        userService.deleteUser(id);
        return new JsonResult<Void>(HttpStatus.NO_CONTENT, null);
    }
}
