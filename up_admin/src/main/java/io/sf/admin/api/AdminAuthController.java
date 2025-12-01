package io.sf.admin.api;

import io.sf.modules.auth.entity.User;
import io.sf.modules.auth.repository.UserRepository;
import io.sf.modules.auth.service.impl.UserService;
import io.sf.config.security.jwt.JwtTokenService;
import io.sf.utils.response.JsonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/auth")
@Tag(name = "Admin 认证", description = "Admin 登录与调试创建管理员接口")
public class AdminAuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${server.debug:false}")
    private boolean debugEnabled;

    @PostMapping("/login")
    @Operation(summary = "登录并返回 JWT")
    public JsonResult<Map<String, Object>> login(@RequestBody Map<String, String> body) throws Exception {
        String username = body.get("username");
        String password = body.get("password");
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        String token = jwtTokenService.generateToken(username);
        io.sf.modules.auth.security.CustomUserDetail cud = (io.sf.modules.auth.security.CustomUserDetail) auth.getPrincipal();
        User user = cud.getUser();
        if (!user.getIsStaff()) {
            return new JsonResult<>(HttpStatus.FORBIDDEN, null);
        }

        if (!cud.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_STAFF"))) {
            return new JsonResult<>(HttpStatus.FORBIDDEN, null);
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("enabled", user.getEnabled());
        userInfo.put("isStaff", user.getIsStaff());
        userInfo.put("tenantId", user.getTenantId());
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", userInfo);
        return new JsonResult<>(HttpStatus.OK, data);
    }

    @GetMapping("/profile")
    @Operation(summary = "获取当前用户信息")
    public JsonResult<Map<String, Object>> profile(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof io.sf.modules.auth.security.CustomUserDetail)) {
            return new JsonResult<>(HttpStatus.UNAUTHORIZED, null);
        }
        io.sf.modules.auth.security.CustomUserDetail cud = (io.sf.modules.auth.security.CustomUserDetail) authentication.getPrincipal();
        User user = cud.getUser();
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("enabled", user.getEnabled());
        userInfo.put("tenantId", user.getTenantId());
        return new JsonResult<>(HttpStatus.OK, userInfo);
    }

    @PostMapping("/debug/create-admin")
    @Operation(summary = "调试环境创建默认管理员")
    public JsonResult<Object> createAdmin() {
        if (!debugEnabled) {
            return new JsonResult<>(HttpStatus.FORBIDDEN, null);
        }
        User existing = userRepository.findByUsername("admin");
        if (existing != null) {
            return new JsonResult<>(HttpStatus.OK, existing.getId());
        }
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setEnabled(Boolean.TRUE);
        admin.setIsStaff(Boolean.TRUE);
        User saved = userRepository.save(admin);
        return new JsonResult<>(HttpStatus.CREATED, saved.getId());
    }
}

