package io.sf.utils.vite.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ViteService {
    private final ViteProperties properties;
    private final ObjectMapper objectMapper;
    private final AtomicReference<Map<String, ManifestEntry>> manifestRef = new AtomicReference<>();

    public ViteService(ViteProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    private final String scriptTagFormatStr = "<script type=\"module\" src=\"%s\"></script>";
    private final String styleTagFormatStr = "<link rel=\"stylesheet\" href=\"%s\">";

    public String script(String entry) {
        if (properties.isDebug()) return properties.getBaseUrl() + "/" + entry;
        ManifestEntry e = getManifest().get(entry);
        if (e == null || e.file == null) return "";
        return resolveAsset(e.file);
    }

    public String style(String entry) {
        if (properties.isDebug()) return properties.getBaseUrl() + "/" + entry;
        ManifestEntry e = getManifest().get(entry);
        if (e == null || e.file == null) return "";
        return resolveAsset(e.file);
    }

    public String scriptTag(String entry) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(scriptTagFormatStr, script(entry)));
        ManifestEntry e = getManifest().get(entry);
        if (e == null || e.css == null || e.css.isEmpty()) return "";
        for (String css : e.css) {
            sb.append(String.format(styleTagFormatStr, css));
        }
        return sb.toString();
    }

    public String styleTag(String entry) {
        return String.format(styleTagFormatStr, style(entry));
    }

    private Map<String, ManifestEntry> getManifest() {
        Map<String, ManifestEntry> current = manifestRef.get();
        if (current != null) return current;
        Path publicDir = resolvePublicDir();
        File manifest = publicDir.resolve("manifest.json").toFile();
        try {
            Map<String, ManifestEntry> m = objectMapper.readValue(manifest, objectMapper.getTypeFactory().constructMapType(Map.class, String.class, ManifestEntry.class));
            manifestRef.compareAndSet(null, m);
            return m;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public Path resolvePublicDir() {
        if (properties.getPublicDir() != null && !properties.getPublicDir().isBlank()) return Paths.get(properties.getPublicDir()).toAbsolutePath();
        Path root = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
        return root.resolve("web").resolve("public");
    }

    public String resolveAsset(String asset) {
        return "/" + asset;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ManifestEntry {
        public String file;
        public List<String> css;
        public boolean isEntry;
    }
}