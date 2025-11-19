package io.sf.app.views.controller;

import io.sf.app.api.auth.controller.dto.LoginRequest;
import io.sf.modules.auth.entity.User;
import io.sf.modules.auth.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.HashMap;

@Slf4j
@Controller
@RequestMapping("")
public class HomeController {

    @Autowired
    private IUserService userService;

    @Autowired
    private DaoAuthenticationProvider authenticationProvider;

    @Autowired
    private RememberMeServices rememberMeServices;

    @GetMapping("/")
    public String home(Model model) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", "Hello, World!");
        // 获取spring security 当前登录用户
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAnonymous = authentication instanceof AnonymousAuthenticationToken;

        result.put("isAnonymous", isAnonymous);
        result.put("user", isAnonymous ? "Anonymous" : authentication.getPrincipal());
        model.addAttribute("result", result);
        return "home";
    }

    // login page
    @GetMapping("/login")
    public String login(Model model,HttpSession session) {return "login";}

    // register page
    @GetMapping("/register")
    public String register(Model model) {
        return "register";
    }

    @GetMapping("/profile")
    public String me(HttpServletRequest request, Model model) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            model.addAttribute("user", auth.getPrincipal());
        } else {
            model.addAttribute("error", "Unauthenticated");
        }
        return "me";
    }

    @PostMapping("/register")
    public String registerUserPost(@ModelAttribute LoginRequest request, HttpServletResponse response,
            RedirectAttributes redirectAttributes,
            HttpSession session) throws Exception {
        try {
            userService.registerUser(
                    new User(null, request.getUsername(), request.getPassword(), Boolean.TRUE, 1l, null, null));
            return "redirect:/";
        } catch (DuplicateKeyException e) {
            redirectAttributes.addFlashAttribute("error", "用户名不可用");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/register";
    }

    // login
    @PostMapping("/login")
    public String loginPost(Model model,
            HttpServletRequest request,
            HttpServletResponse response,
            @ModelAttribute LoginRequest loginRequest,
            HttpSession session,
                          RedirectAttributes redirectAttributes
                          ) throws IOException {
        try {
            var authentication = authenticationProvider
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                            loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // 将 SecurityContext 写入 session
            session.setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext());
            // 处理记住我功能
            rememberMeServices.loginSuccess(request, response, authentication);
            redirectAttributes.addFlashAttribute("success","您已登入");
            return "redirect:/";
        } catch (BadCredentialsException e) {
            redirectAttributes.addFlashAttribute("error", "用户名或密码错误");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "登录失败");
        }

        return "redirect:/login";

    }

}