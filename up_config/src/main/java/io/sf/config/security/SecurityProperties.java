package io.sf.config.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {
    private Boolean loginFormEnabled = true;
    private Boolean logoutEnabled = true;
    private String[] whitelist = new String[0];
    private JwtProperties jwt = new JwtProperties();
    private RememberMeProperties rememberMe = new RememberMeProperties();



    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class JwtProperties {
        private String secret = "uniqueAndSecret12123";
        private Long expiration = 86400000L;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class RememberMeProperties {
        private String key = "uniqueAndSecret12123";
        private Long expiration = 86400000L;
    }
}