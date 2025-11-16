package io.sf.app.views.advice;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Year;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class SiteInfoControllerAdvice {
    @ModelAttribute("site")
    public Map<String, Object> site(HttpServletRequest request) {
        Map<String, Object> site = new HashMap<>();
        site.put("name", "认证系统");
        site.put("year", Year.now().getValue());
        site.put("home", "/");
        site.put("title", "认证系统");
        site.put("description", "基于 Spring Boot、Spring Security、JWT 的认证平台");
        site.put("keywords", "认证, 登录, 注册, Spring Boot, Spring Security, JWT");
        site.put("robots", "index,follow");
        site.put("author", "Startup");
        site.put("lang", "zh-CN");
        site.put("copyright", "© 2023 Startup");
        String canonical = "";
        if (request != null) {
            var host = request.getHeader("Host");
            canonical = host;
        }
        site.put("canonical", canonical);
        return site;
    }
}