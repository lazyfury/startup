package io.sf.admin.api;

import io.sf.modules.menu.service.MenuPermissionService;
import io.sf.modules.acl.entity.Permission;
import io.sf.modules.config.entity.ScopeType;
import io.sf.modules.menu.entity.Menu;
import io.sf.modules.menu.repository.MenuRepository;
import io.sf.utils.crud.CrudApiController;
import io.sf.utils.response.JsonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("menu")
@Tag(name = "系统配置/后台菜单管理", description = "后台路由菜单管理接口（支持树形）")
public class MenuController extends CrudApiController<Menu, Long, MenuRepository> {
    private final MenuPermissionService menuPermissionService;

    protected MenuController(MenuRepository repository, MenuPermissionService menuPermissionService) {
        super(repository);
        this.menuPermissionService = menuPermissionService;
    }

    @Override
    protected Class<Menu> entityClass() {
        return Menu.class;
    }

    @GetMapping("/tree")
    @Operation(summary = "获取菜单树")
    public JsonResult<List<Menu>> tree(@RequestParam(required = false) Boolean includeDisabled) {
        List<Menu> all = Boolean.TRUE.equals(includeDisabled)
                ? repository.findAllByOrderByOrderNoAsc()
                : repository.findAllByStatusIsTrueOrderByOrderNoAsc();
        Map<Long, Menu> map = new HashMap<>();
        List<Menu> roots = new ArrayList<>();
        for (Menu m : all) {
            m.setChildren(new ArrayList<>());
            map.put(m.getId(), m);
        }
        for (Menu m : all) {
            Long pid = m.getParentId();
            if (pid == null) {
                roots.add(m);
            } else {
                Menu parent = map.get(pid);
                if (parent != null) parent.getChildren().add(m);
                else roots.add(m);
            }
        }
        return new JsonResult<>(HttpStatus.OK, roots);
    }

    @GetMapping("/{id}/permissions")
    @Operation(summary = "查询菜单的权限")
    public JsonResult<List<Permission>> listPermissions(@NonNull @PathVariable Long id,
                                                        @RequestParam(required = false) String scopeType,
                                                        @RequestParam(required = false) Long scopeId) {
        ScopeType st = null;
        if (scopeType != null) {
            try { st = ScopeType.valueOf(scopeType.toUpperCase()); } catch (Exception ignored) {}
        }
        List<Permission> list = menuPermissionService.listMenuPermissions(id, st, scopeId);
        return new JsonResult<>(HttpStatus.OK, list);
    }

    @PostMapping("/{id}/permissions/auto")
    @Operation(summary = "为菜单自动创建权限")
    public JsonResult<List<Permission>> autoCreatePermissions(@NonNull @PathVariable Long id,
                                                             @RequestBody(required = false) Map<String, Object> body) {
        List<String> actions;
        List<String> defaultActions = Arrays.asList("view", "create", "update", "delete");
        if (body != null && body.get("actions") instanceof List<?> list) {
            actions = list.stream().map(Object::toString).toList();
        } else {
            actions = defaultActions;
        }
        ScopeType st = ScopeType.SYSTEM;
        Long sid = null;
        if (body != null) {
            Object ost = body.get("scopeType");
            if (ost instanceof String) {
                try { st = ScopeType.valueOf(((String) ost).toUpperCase()); } catch (Exception ignored) {}
            }
            Object osid = body.get("scopeId");
            if (osid instanceof Number) sid = ((Number) osid).longValue();
        }

        List<Permission> created = menuPermissionService.autoCreate(id, st, sid, actions);
        if (created.isEmpty()) {
            return new JsonResult<>(HttpStatus.OK, created);
        }
        return new JsonResult<>(HttpStatus.CREATED, created);
    }
}
