package io.sf.admin.api;

import io.sf.modules.acl.entity.Role;
import io.sf.modules.acl.repository.RoleRepository;
import io.sf.utils.crud.CrudApiController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("role")
@Tag(name = "用户权限/角色管理", description = "角色管理接口")
public class RoleController extends CrudApiController<Role, Long, RoleRepository> {
    protected RoleController(RoleRepository repository) {
        super(repository);
    }

    @Override
    protected Class<Role> entityClass() {
        return Role.class;
    }
}

