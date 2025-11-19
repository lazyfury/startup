package io.sf.config.security;

import java.util.HashMap;

import io.sf.utils.response.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices.RememberMeTokenAlgorithm;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.sf.config.security.jwt.JwtTokenService.JwtAuthFilter;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private ObjectMapper objectMapper;


    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(java.util.List.of("*"));
        config.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(java.util.List.of("*"));
        config.setExposedHeaders(java.util.List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
            DaoAuthenticationProvider authenticationProvider,
            JwtAuthFilter jwtAuthFilter,
            UserDetailsService userDetailsService) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authenticationProvider(authenticationProvider)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/login", "/register").permitAll()
                        .requestMatchers("/api/auth/login", "/api/auth/register").permitAll()

                        .requestMatchers("/assets/**").permitAll()
                        .requestMatchers("/third/bootstrap/**").permitAll()
                        .requestMatchers("/public/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/v3/api-docs.yaml")
                        .permitAll()
                        .requestMatchers(securityProperties.getWhitelist()).permitAll()
                        .anyRequest().authenticated())
                .rememberMe(rememberMe -> rememberMe
                        .rememberMeServices(rememberMeServices(userDetailsService))
                        .key(securityProperties.getRememberMe().getKey())
                        .tokenValiditySeconds(securityProperties.getRememberMe().getExpiration().intValue()))
                .formLogin(securityProperties.getLoginFormEnabled() ? form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/_security_login")
                        .defaultSuccessUrl("/me", true)
                        .failureHandler(customAuthenticationFailureHandler())
                        .permitAll() : AbstractHttpConfigurer::disable)
                .logout(securityProperties.getLogoutEnabled() ? logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .permitAll() : AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            log.debug("访问被拒绝：{}", accessDeniedException.getMessage());
                            var wantJson = request.getHeader("Content-Type") != null
                                    && request.getHeader("Content-Type").contains("application/json");
                            if (wantJson) {
                                HashMap<String,Object> extra = new HashMap<>();
                                extra.put("ExceptionHandler", "Spring Security AccessDenied");
                                JsonResult<String> result = new JsonResult<>(HttpServletResponse.SC_FORBIDDEN, null, "Forbidden",extra);
                                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                response.setContentType("application/json");
                                response.getWriter().write(objectMapper.writeValueAsString(result));
                            } else {
                                response.sendRedirect("/403");
                            }
                        }).authenticationEntryPoint((request, response, authException) -> {
                            log.debug("未认证请求：{}", authException.getMessage());
                            var wantJson = request.getHeader("Content-Type") != null
                                    && request.getHeader("Content-Type").contains("application/json");
                            if (wantJson) {
                                HashMap<String,Object> extra = new HashMap<>();
                                extra.put("ExceptionHandler", "Spring Security Unauthorized");
                                JsonResult<String> result = new JsonResult<>(HttpServletResponse.SC_UNAUTHORIZED, null, "Unauthorized");
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                response.setContentType("application/json");
                                response.getWriter().write(objectMapper.writeValueAsString(result));
                            } else {
                                response.sendRedirect("/login");
                            }
                        }))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    RememberMeServices rememberMeServices(UserDetailsService userDetailsService) {
        RememberMeTokenAlgorithm encodingAlgorithm = RememberMeTokenAlgorithm.SHA256;
        TokenBasedRememberMeServices rememberMe = new TokenBasedRememberMeServices(securityProperties.getRememberMe().getKey(),
                userDetailsService,
                encodingAlgorithm);
        rememberMe.setMatchingAlgorithm(RememberMeTokenAlgorithm.MD5);
        return rememberMe;
    }

    @Bean
    public AuthenticationFailureHandler customAuthenticationFailureHandler() {
        return (request, response, exception) -> {
            String contextType = request.getHeader("Content-Type");
            if (contextType != null && contextType.contains("application/json")) {
                HashMap<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Authentication failed");

                if (exception instanceof org.springframework.security.authentication.BadCredentialsException) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    errorResponse.put("message", "Invalid username or password");
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    errorResponse.put("message", exception.getMessage());
                }
                String json = new ObjectMapper().writeValueAsString(errorResponse);
                response.getWriter().write(json);
                response.getWriter().flush();
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
            } else {
                response.sendRedirect("/login?error");
            }
        };
    }
}