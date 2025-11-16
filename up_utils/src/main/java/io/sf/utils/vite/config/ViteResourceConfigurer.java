package io.sf.utils.vite.config;

import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ViteResourceConfigurer implements WebMvcConfigurer {
    @Autowired
    private ViteService viteService;
    @Autowired
    private ViteProperties viteProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path publicDir = viteService.resolvePublicDir();
        registry.addResourceHandler("/" + viteProperties.getAssetsDir() + "/**")
                .addResourceLocations(publicDir.resolve(viteProperties.getAssetsDir()).toUri().toString());
        registry.addResourceHandler("/manifest.json")
                .addResourceLocations(publicDir.resolve("manifest.json").toUri().toString());

        registry.addResourceHandler("/third/bootstrap/**")
                .addResourceLocations(publicDir.resolve("third/bootstrap").toUri().toString());
    }
}