package io.sf.admin.api;

import io.sf.modules.acl.entity.Permission;
import io.sf.modules.acl.repository.PermissionRepository;
import io.sf.modules.config.entity.ScopeType;
import io.sf.utils.crud.CrudApiController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.sf.utils.response.JsonResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.HttpStatus;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("permission")
@Tag(name = "用户权限/权限管理", description = "权限管理接口")
public class PermissionController extends CrudApiController<Permission, Long, PermissionRepository> {
    protected PermissionController(PermissionRepository repository) {
        super(repository);
    }

    @Override
    protected Class<Permission> entityClass() {
        return Permission.class;
    }

    @GetMapping("/tags")
    @Operation(summary = "查询权限标签列表")
    public JsonResult<java.util.List<String>> tags(@RequestParam(required = false) String scopeType,
                                                   @RequestParam(required = false) Long scopeId) {
        if (scopeType != null) {
            try {
                ScopeType st = ScopeType.valueOf(scopeType.toUpperCase());
                return new JsonResult<>(HttpStatus.OK, repository.findDistinctTagsByScope(st, scopeId));
            } catch (Exception ignored) {}
        }
        return new JsonResult<>(HttpStatus.OK, repository.findDistinctTags());
    }

    @GetMapping("/search-by-tag")
    @Operation(summary = "按标签快速搜索权限")
    public JsonResult<java.util.List<Permission>> searchByTag(@RequestParam String q,
                                                              @RequestParam(required = false) String scopeType,
                                                              @RequestParam(required = false) Long scopeId,
                                                              @RequestParam(required = false, defaultValue = "20") Integer limit) {
        String prefix = q == null ? "" : q.trim();
        java.util.List<Permission> list;
        if (scopeType != null) {
            try {
                ScopeType st = ScopeType.valueOf(scopeType.toUpperCase());
                list = repository.findAllByTagStartingWithIgnoreCaseAndScopeTypeAndScopeId(prefix, st, scopeId);
            } catch (Exception e) {
                list = repository.findAllByTagStartingWithIgnoreCase(prefix);
            }
        } else {
            list = repository.findAllByTagStartingWithIgnoreCase(prefix);
        }
        if (limit != null && limit > 0 && list.size() > limit) {
            list = list.subList(0, limit);
        }
        return new JsonResult<>(HttpStatus.OK, list);
    }

    @GetMapping("/tree")
    @Operation(summary = "获取权限树")
    public JsonResult<List<Permission>> tree(@RequestParam(required = false) String scopeType,
                                             @RequestParam(required = false) Long scopeId) {
        List<Permission> all;
        if (scopeType != null) {
            try {
                ScopeType st = ScopeType.valueOf(scopeType.toUpperCase());
                all = repository.findAllByScopeTypeAndScopeId(st, scopeId);
            } catch (Exception e) {
                all = repository.findAll();
            }
        } else {
            all = repository.findAll();
        }
        Map<Long, Permission> map = new HashMap<>();
        List<Permission> roots = new ArrayList<>();
        for (Permission p : all) {
            p.setChildren(new ArrayList<>());
            map.put(p.getId(), p);
        }
        for (Permission p : all) {
            Long pid = p.getParentId();
            if (pid == null) {
                roots.add(p);
            } else {
                Permission parent = map.get(pid);
                if (parent != null) parent.getChildren().add(p);
                else roots.add(p);
            }
        }
        return new JsonResult<>(HttpStatus.OK, roots);
    }
}
