package io.sf.utils.vite.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
@ConfigurationProperties(prefix = "vite")
public class ViteProperties {
    private String baseUrl = "http://127.0.0.1:5173";
    private String publicDir;
    private boolean debug = true;
    private String assetsDir = "assets";
}