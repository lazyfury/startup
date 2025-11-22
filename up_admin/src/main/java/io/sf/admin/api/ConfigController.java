package io.sf.admin.api;

import io.sf.modules.config.entity.ConfigSetting;
import io.sf.modules.config.repository.ConfigSettingRepository;
import io.sf.utils.crud.CrudApiController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("config")
public class ConfigController extends CrudApiController<ConfigSetting,Long, ConfigSettingRepository> {
    protected ConfigController(ConfigSettingRepository repository) {
        super(repository);
    }

    @Override
    protected Class<ConfigSetting> entityClass() {
        return ConfigSetting.class;
    }
}
