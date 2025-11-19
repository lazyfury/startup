package io.sf.modules.config.service.impl;

import io.sf.modules.config.entity.ConfigSetting;
import io.sf.modules.config.entity.ScopeType;
import io.sf.modules.config.repository.ConfigSettingRepository;
import io.sf.modules.config.service.IConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

@Service
public class ConfigService implements IConfigService {

    @Autowired
    private ConfigSettingRepository repository;

    @Override
    @PreAuthorize("#scopeType != T(io.sf.modules.config.entity.ScopeType).SYSTEM or hasAuthority('CONFIG_SYSTEM')")
    public Optional<ConfigSetting> getByKeyAndScopeType(String key, ScopeType scopeType) {
        if (scopeType != ScopeType.SYSTEM) {
            throw new IllegalArgumentException("scope_id required for non-SYSTEM scope_type");
        }
        return repository.findByKeyAndScopeTypeAndStatusIsTrue(key, scopeType);
    }

    @Override
    @PreAuthorize("#scopeType != T(io.sf.modules.config.entity.ScopeType).SYSTEM or hasAuthority('CONFIG_SYSTEM')")
    public Optional<ConfigSetting> getByKeyScope(String key, ScopeType scopeType, Long scopeId) {
        if (scopeType != ScopeType.SYSTEM && scopeId == null) {
            throw new IllegalArgumentException("scope_id required for non-SYSTEM scope_type");
        }
        if (scopeId != null) {
            return repository.findByKeyAndScopeTypeAndScopeIdAndStatusIsTrue(key, scopeType, scopeId);
        }
        return repository.findByKeyAndScopeTypeAndStatusIsTrue(key, scopeType);
    }

    @Override
    @PreAuthorize("#setting.scopeType != T(io.sf.modules.config.entity.ScopeType).SYSTEM or hasAuthority('CONFIG_SYSTEM')")
    public ConfigSetting saveOrUpdate(ConfigSetting setting) {
        if (setting.getScopeType() != ScopeType.SYSTEM && setting.getScopeId() == null) {
            throw new IllegalArgumentException("scope_id required for non-SYSTEM scope_type");
        }
        Optional<ConfigSetting> existing = setting.getScopeId() != null
                ? repository.findByKeyAndScopeTypeAndScopeIdAndStatusIsTrue(setting.getKey(), setting.getScopeType(), setting.getScopeId())
                : repository.findByKeyAndScopeTypeAndStatusIsTrue(setting.getKey(), setting.getScopeType());
        if (existing.isPresent()) {
            ConfigSetting found = existing.get();
            found.setValue(setting.getValue());
            found.setType(setting.getType());
            found.setStatus(setting.getStatus());
            return repository.save(found);
        }
        return repository.save(setting);
    }

    @Override
    @PreAuthorize("#scopeType != T(io.sf.modules.config.entity.ScopeType).SYSTEM or hasAuthority('CONFIG_SYSTEM')")
    public void deactivate(String key, ScopeType scopeType) {
        if (scopeType != ScopeType.SYSTEM) {
            throw new IllegalArgumentException("scope_id required for non-SYSTEM scope_type");
        }
        Optional<ConfigSetting> existing = repository.findByKeyAndScopeTypeAndStatusIsTrue(key, scopeType);
        existing.ifPresent(cfg -> {
            cfg.setStatus(Boolean.FALSE);
            repository.save(cfg);
        });
    }

    @Override
    @PreAuthorize("#scopeType != T(io.sf.modules.config.entity.ScopeType).SYSTEM or hasAuthority('CONFIG_SYSTEM')")
    public void deactivate(String key, ScopeType scopeType, Long scopeId) {
        if (scopeType != ScopeType.SYSTEM && scopeId == null) {
            throw new IllegalArgumentException("scope_id required for non-SYSTEM scope_type");
        }
        Optional<ConfigSetting> existing = scopeId != null
                ? repository.findByKeyAndScopeTypeAndScopeIdAndStatusIsTrue(key, scopeType, scopeId)
                : repository.findByKeyAndScopeTypeAndStatusIsTrue(key, scopeType);
        existing.ifPresent(cfg -> {
            cfg.setStatus(Boolean.FALSE);
            repository.save(cfg);
        });
    }
}