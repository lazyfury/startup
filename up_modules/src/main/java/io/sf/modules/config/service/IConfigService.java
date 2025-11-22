package io.sf.modules.config.service;

import io.sf.modules.config.entity.ConfigSetting;
import io.sf.modules.config.entity.ScopeType;
import java.util.List;

import java.util.Optional;

public interface IConfigService {
    Optional<ConfigSetting> getByKeyAndScopeType(String key, ScopeType scopeType);
    Optional<ConfigSetting> getByKeyScope(String key, ScopeType scopeType, Long scopeId);
    ConfigSetting saveOrUpdate(ConfigSetting setting);
    void deactivate(String key, ScopeType scopeType);
    void deactivate(String key, ScopeType scopeType, Long scopeId);
    List<ConfigSetting> getByGroup(Long groupId);
    List<ConfigSetting> saveOrUpdateByGroup(Long groupId, List<ConfigSetting> settings);
    void deactivateGroup(Long groupId);
}
