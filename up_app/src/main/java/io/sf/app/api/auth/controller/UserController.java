package io.sf.app.api.auth.controller;

import io.sf.app.api.auth.controller.dto.LoginRequest;
import io.sf.app.api.auth.controller.dto.LoginResult;
import io.sf.app.api.auth.controller.dto.RegisterResult;
import io.sf.config.security.jwt.JwtTokenService;
import io.sf.modules.auth.entity.User;
import io.sf.modules.auth.security.CustomUserDetail;
import io.sf.modules.auth.service.IUserService;
import io.sf.utils.response.JsonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "用户管理", description = "用户相关操作")
@Slf4j
@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private DaoAuthenticationProvider authenticationProvider;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Operation(summary = "根据用户名获取用户信息", description = "根据用户名获取用户信息", tags = { "用户管理" })
    @ApiResponse(responseCode = "200", description = "成功获取用户信息")
    @GetMapping("/getByUsername")
    public HashMap<String, Object> getUserByUsername(@RequestParam("username") String username) {
        HashMap<String, Object> result = new HashMap<>();
        User user = userService.getUserByUsername(username).orElse(null);
        if (user == null) {
            result.put("error", "User not found");
        } else {
            result.put("user", user);
        }
        return result;
    }

    @PostMapping("/register")
    public JsonResult<RegisterResult> registerUserPost(@RequestBody LoginRequest request) throws Exception {
        int affectedRows = userService.registerUser(
                new User(null, request.getUsername(), request.getPassword(), Boolean.TRUE, 1l, null, null));
        if (affectedRows <= 0) {
            throw new Exception("注册账号错误");
        }
        User user = userService.getUserByUsername(request.getUsername()).orElseThrow();
        Authentication authentication = null;
        authentication = authenticationProvider
                .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        CustomUserDetail userDetail = (CustomUserDetail) authentication.getPrincipal();
        var token = jwtTokenService.generateToken(request.getUsername());
        RegisterResult result = new RegisterResult(user.getId(), userDetail, token);
        return new JsonResult<RegisterResult>(200, result, "ok");

    }

    @PostMapping("/login")
    public JsonResult<LoginResult> loginUser(
            HttpServletRequest request, HttpServletResponse response,
            @RequestBody LoginRequest loginData) throws Exception {

        var authentication = authenticationProvider
                .authenticate(
                        new UsernamePasswordAuthenticationToken(loginData.getUsername(), loginData.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        CustomUserDetail user = (CustomUserDetail) authentication.getPrincipal();
        var token = jwtTokenService.generateToken(loginData.getUsername());
        return new JsonResult<LoginResult>(200, new LoginResult(token, user), "ok");
    }

    @GetMapping("/me")
    public JsonResult<CustomUserDetail> profile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetail user = (CustomUserDetail) auth.getPrincipal();
        return new JsonResult<CustomUserDetail>(200, user, "");
    }

    @GetMapping("/ex")
    public String getMethodName(@RequestParam String param) throws Exception {
        throw new Exception("测试security拦截异常");
    }

}
