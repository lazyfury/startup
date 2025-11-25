package io.sf.admin.api;

import io.sf.modules.config.entity.ConfigGroup;
import io.sf.modules.config.repository.ConfigGroupRepository;
import io.sf.modules.config.service.IConfigService;
import io.sf.utils.crud.CrudApiController;
import io.sf.utils.response.JsonResult;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("config-group")
@Tag(name = "系统配置/配置分组管理",description = "配置分组管理接口")
public class ConfigGroupController extends CrudApiController<ConfigGroup, Long, ConfigGroupRepository> {
    protected ConfigGroupController(ConfigGroupRepository repository) {
        super(repository);
    }

    @Override
    protected Class<ConfigGroup> entityClass() {
        return ConfigGroup.class;
    }

    @Autowired
    private IConfigService configService;

    @Override
    @DeleteMapping("/{id}")
    public JsonResult<Void> delete(@PathVariable Long id) {
        try {
            configService.deactivateGroup(id);
            return new JsonResult<Void>(HttpStatus.NO_CONTENT, null);
        } catch (IllegalStateException ex) {
            return new JsonResult<>(400, null, ex.getMessage());
        }
    }
}

