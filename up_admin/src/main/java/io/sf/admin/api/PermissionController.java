package io.sf.admin.api;

import io.sf.modules.acl.entity.Permission;
import io.sf.modules.acl.repository.PermissionRepository;
import io.sf.utils.crud.CrudApiController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

