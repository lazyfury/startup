package io.sf.admin.api;

import io.sf.modules.tenant.entity.Tenant;
import io.sf.modules.tenant.repository.TenantRepository;
import io.sf.utils.crud.CrudApiController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("tenant")
@Tag(name = "组织管理", description = "组织(Tenant)管理接口")
public class TenantController extends CrudApiController<Tenant, Long, TenantRepository> {
    protected TenantController(TenantRepository repository) {
        super(repository);
    }

    @Override
    protected Class<Tenant> entityClass() {
        return Tenant.class;
    }
}

