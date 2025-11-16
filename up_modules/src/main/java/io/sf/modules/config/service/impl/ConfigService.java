package io.sf.modules.config.service.impl;

import io.sf.modules.config.entity.ConfigSetting;
import io.sf.modules.config.repository.ConfigSettingRepository;
import io.sf.modules.config.service.IConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConfigService implements IConfigService {

    private final ConfigSettingRepository repository;

    @Override
    public Optional<ConfigSetting> getGlobal(String key) {
        return repository.findByTenantIdIsNullAndMerchantIdIsNullAndKey(key);
    }

    @Override
    public Optional<ConfigSetting> getForTenant(Long tenantId, String key) {
        return repository.findByTenantIdAndMerchantIdIsNullAndKey(tenantId, key);
    }

    @Override
    public Optional<ConfigSetting> getForMerchant(Long merchantId, String key) {
        return repository.findByMerchantIdAndKey(merchantId, key);
    }

    @Override
    public List<ConfigSetting> listTenantConfigs(Long tenantId) {
        return repository.findAllByTenantIdAndMerchantIdIsNull(tenantId);
    }

    @Override
    public List<ConfigSetting> listMerchantConfigs(Long merchantId) {
        return repository.findAllByMerchantId(merchantId);
    }

    @Override
    @Transactional
    public ConfigSetting upsertGlobal(String key, String value, String valueType, String description, Boolean enabled) {
        ConfigSetting cfg = repository.findByTenantIdIsNullAndMerchantIdIsNullAndKey(key)
                .orElseGet(ConfigSetting::new);
        cfg.setTenantId(null);
        cfg.setMerchantId(null);
        cfg.setKey(key);
        cfg.setValue(value);
        cfg.setValueType(valueType);
        cfg.setDescription(description);
        cfg.setEnabled(enabled != null ? enabled : Boolean.TRUE);
        return repository.save(cfg);
    }

    @Override
    @Transactional
    public ConfigSetting upsertTenant(Long tenantId, String key, String value, String valueType, String description, Boolean enabled) {
        ConfigSetting cfg = repository.findByTenantIdAndMerchantIdIsNullAndKey(tenantId, key)
                .orElseGet(ConfigSetting::new);
        cfg.setTenantId(tenantId);
        cfg.setMerchantId(null);
        cfg.setKey(key);
        cfg.setValue(value);
        cfg.setValueType(valueType);
        cfg.setDescription(description);
        cfg.setEnabled(enabled != null ? enabled : Boolean.TRUE);
        return repository.save(cfg);
    }

    @Override
    @Transactional
    public ConfigSetting upsertMerchant(Long merchantId, String key, String value, String valueType, String description, Boolean enabled) {
        ConfigSetting cfg = repository.findByMerchantIdAndKey(merchantId, key)
                .orElseGet(ConfigSetting::new);
        cfg.setTenantId(null); // 如果需要租户上下文，可在调用方传入并扩展表设计
        cfg.setMerchantId(merchantId);
        cfg.setKey(key);
        cfg.setValue(value);
        cfg.setValueType(valueType);
        cfg.setDescription(description);
        cfg.setEnabled(enabled != null ? enabled : Boolean.TRUE);
        return repository.save(cfg);
    }
}