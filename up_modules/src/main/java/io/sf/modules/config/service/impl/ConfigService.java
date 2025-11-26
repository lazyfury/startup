package io.sf.modules.config.service.impl;

import io.sf.modules.config.entity.ConfigSetting;
import io.sf.modules.config.entity.ScopeType;
import io.sf.modules.config.repository.ConfigSettingRepository;
import io.sf.modules.config.entity.ConfigGroup;
import io.sf.modules.config.repository.ConfigGroupRepository;
import io.sf.modules.config.service.IConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;
import java.util.List;
import java.util.Objects;

@Service
public class ConfigService implements IConfigService {

    @Autowired
    private ConfigSettingRepository repository;

    @Autowired
    private ConfigGroupRepository groupRepository;

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

    @Override
    public List<ConfigSetting> getByGroup(Long groupId) {
        return repository.findAllByGroupIdAndStatusIsTrue(groupId);
    }

    @Override
    public List<ConfigSetting> saveOrUpdateByGroup(Long groupId, List<ConfigSetting> settings) {
        Objects.requireNonNull(groupId);
        Optional<ConfigGroup> groupOpt = groupRepository.findById(groupId);
        if (groupOpt.isEmpty()) {
            throw new IllegalArgumentException("config_group not found");
        }
        ConfigGroup group = groupOpt.get();
        for (ConfigSetting s : settings) {
            s.setGroupId(groupId);
            s.setScopeType(group.getScopeType());
            s.setScopeId(group.getScopeId());
            Optional<ConfigSetting> existing = s.getScopeId() != null
                    ? repository.findByKeyAndScopeTypeAndScopeIdAndStatusIsTrue(s.getKey(), s.getScopeType(), s.getScopeId())
                    : repository.findByKeyAndScopeTypeAndStatusIsTrue(s.getKey(), s.getScopeType());
            if (existing.isPresent()) {
                ConfigSetting found = existing.get();
                found.setValue(s.getValue());
                found.setType(s.getType());
                found.setStatus(s.getStatus());
                found.setGroupId(groupId);
                repository.save(found);
            } else {
                repository.save(s);
            }
        }
        return repository.findAllByGroupIdAndStatusIsTrue(groupId);
    }

    @Override
    public void deactivateGroup(Long groupId) {
        long count = repository.countByGroupIdAndStatusIsTrue(groupId);
        if (count > 0) {
            throw new IllegalStateException("config_group has settings");
        }
        if (groupId == null) {
            throw new IllegalArgumentException("group_id required");
        }
        Optional<ConfigGroup> groupOpt = groupRepository.findById(groupId);
        groupOpt.ifPresent(g -> {
            g.setStatus(Boolean.FALSE);
            groupRepository.save(g);
        });
    }
}
