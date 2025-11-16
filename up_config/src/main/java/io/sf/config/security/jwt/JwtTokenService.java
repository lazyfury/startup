package io.sf.config.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.sf.config.security.SecurityProperties;
import io.sf.modules.auth.entity.User;
import io.sf.modules.auth.security.CustomUserDetail;
import io.sf.modules.auth.service.IUserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;


@Slf4j
@Component
public class JwtTokenService {

    @Autowired
    private SecurityProperties securityProperties;


    public String generateToken(String username) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(securityProperties.getJwt().getExpiration())))
                .signWith(Keys.hmacShaKeyFor(securityProperties.getJwt().getSecret().getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public String parseUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(securityProperties.getJwt().getSecret().getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    @Component
    public static class JwtAuthFilter extends OncePerRequestFilter {

        @Autowired
        private JwtTokenService tokenService;

        @Autowired
        private IUserService userService;

        @Override

        protected void doFilterInternal(@NonNull HttpServletRequest request,
                @NonNull HttpServletResponse response,
                @NonNull FilterChain filterChain) throws ServletException, IOException {
            String authHeader = request.getHeader("Authorization");
            if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                try {
                    String username = tokenService.parseUsername(token);
                    Optional<User> userOpt = userService.getUserByUsername(username);
                    if (userOpt.isPresent() && SecurityContextHolder.getContext().getAuthentication() == null) {
                        User user = userOpt.get();
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                CustomUserDetail.from(user), null, java.util.Collections.emptyList());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        request.setAttribute("currentUser", user);
                    }
                } catch (Exception ex) {
                    // token 解析失败则跳过，后续由 Security 处理为未认证请求
                    log.debug("解析token失败：{}",ex.getMessage());
                }
            }

            filterChain.doFilter(request, response);
        }
    }

}