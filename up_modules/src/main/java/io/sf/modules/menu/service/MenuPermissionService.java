package io.sf.modules.menu.service;

import io.sf.modules.acl.entity.Permission;
import io.sf.modules.acl.repository.PermissionRepository;
import io.sf.modules.config.entity.ScopeType;
import io.sf.modules.menu.entity.Menu;
import io.sf.modules.menu.repository.MenuRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MenuPermissionService {
    private final PermissionRepository permissionRepository;
    private final MenuRepository menuRepository;

    public MenuPermissionService(PermissionRepository permissionRepository, MenuRepository menuRepository) {
        this.permissionRepository = permissionRepository;
        this.menuRepository = menuRepository;
    }

    public Optional<Menu> findMenu(@NonNull Long id) {
        return menuRepository.findById(id);
    }

    public String baseCode(Menu menu) {
        return "menu:" + menu.getCode();
    }

    public List<Permission> listMenuPermissions(@NonNull Long menuId, ScopeType scopeType, Long scopeId) {
        Optional<Menu> opt = findMenu(menuId);
        if (opt.isEmpty()) return Collections.emptyList();
        String prefix = baseCode(opt.get()) + ":";
        if (scopeType != null) {
            return permissionRepository.findAllByCodeStartingWithAndScopeTypeAndScopeId(prefix, scopeType, scopeId);
        }
        return permissionRepository.findAllByCodeStartingWith(prefix);
    }

    public List<Permission> autoCreate(@NonNull Long menuId, ScopeType scopeType, Long scopeId, List<String> actions) {
        Optional<Menu> opt = findMenu(menuId);
        if (opt.isEmpty()) return Collections.emptyList();
        Menu menu = opt.get();
        String base = baseCode(menu);
        List<Permission> created = new ArrayList<>();
        for (String act : actions) {
            if (act == null || act.isBlank()) continue;
            String code = base + ":" + act.trim().toLowerCase();
            boolean exists = permissionRepository.existsByCodeAndScopeTypeAndScopeId(code, scopeType, scopeId);
            if (exists) continue;
            Permission p = new Permission();
            p.setName(menu.getName() + " " + act);
            p.setCode(code);
            p.setDescription("Auto for " + base);
            p.setTag(menu.getCode());
            p.setScopeType(scopeType != null ? scopeType : ScopeType.SYSTEM);
            p.setScopeId(scopeId);
            p.setStatus(Boolean.TRUE);
            Permission saved = permissionRepository.save(p);
            created.add(saved);
        }
        return created;
    }
}
