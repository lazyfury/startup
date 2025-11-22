package io.sf.admin.api;

import io.sf.modules.config.entity.ConfigSetting;
import io.sf.modules.config.repository.ConfigSettingRepository;
import io.sf.modules.config.service.IConfigService;
import io.sf.utils.crud.CrudApiController;
import io.sf.utils.response.JsonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("config")
@Tag(name = "配置管理",description = "配置管理接口")
public class ConfigController extends CrudApiController<ConfigSetting,Long, ConfigSettingRepository> {
    protected ConfigController(ConfigSettingRepository repository) {
        super(repository);
    }

    @Override
    protected Class<ConfigSetting> entityClass() {
        return ConfigSetting.class;
    }

    @Autowired
    private IConfigService configService;

    @GetMapping("/group/{groupId}")
    @Operation(summary = "根据分组查询配置",description = "根据分组查询配置")
    public JsonResult<java.util.List<ConfigSetting>> listByGroup(@PathVariable Long groupId) {
        return new JsonResult<>(200, configService.getByGroup(groupId), "ok");
    }

    @PostMapping("/group/{groupId}")
    @Operation(summary = "根据分组保存配置",description = "根据分组保存配置")
    public JsonResult<java.util.List<ConfigSetting>> saveByGroup(@PathVariable Long groupId, @RequestBody java.util.List<ConfigSetting> settings) {
        return new JsonResult<>(200, configService.saveOrUpdateByGroup(groupId, settings), "ok");
    }

    @PostMapping("/group/{groupId}/deactivate")
    @Operation(summary = "根据分组停用配置",description = "根据分组停用配置")
    public JsonResult<String> deactivateGroup(@PathVariable Long groupId) {
        try {
            configService.deactivateGroup(groupId);
            return new JsonResult<>(200, null, "ok");
        } catch (IllegalStateException ex) {
            return new JsonResult<>(400, null, ex.getMessage());
        }
    }
}
