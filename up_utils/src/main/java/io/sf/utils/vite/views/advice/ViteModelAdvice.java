package io.sf.utils.vite.views.advice;

import io.sf.utils.vite.config.ViteProperties;
import io.sf.utils.vite.config.ViteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class ViteModelAdvice {
    @Autowired
    private ViteService viteService;

    @Autowired
    private ViteProperties viteProperties;

    @ModelAttribute("vite")
    public ViteService vite() {
        return viteService;
    }

    @ModelAttribute("viteDebug")
    public boolean viteDebug() {
        return viteProperties.isDebug();
    }
}