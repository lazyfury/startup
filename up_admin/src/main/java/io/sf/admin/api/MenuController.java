package io.sf.admin.api;

import io.sf.modules.menu.entity.Menu;
import io.sf.modules.menu.repository.MenuRepository;
import io.sf.utils.crud.CrudApiController;
import io.sf.utils.response.JsonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("menu")
@Tag(name = "后台菜单管理", description = "后台路由菜单管理接口（支持树形）")
public class MenuController extends CrudApiController<Menu, Long, MenuRepository> {
    protected MenuController(MenuRepository repository) {
        super(repository);
    }

    @Override
    protected Class<Menu> entityClass() {
        return Menu.class;
    }

    @GetMapping("/tree")
    @Operation(summary = "获取菜单树")
    public JsonResult<List<Menu>> tree() {
        List<Menu> all = repository.findAllByStatusIsTrueOrderByOrderNoAsc();
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
}

