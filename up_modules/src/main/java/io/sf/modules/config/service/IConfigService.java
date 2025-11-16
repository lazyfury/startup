package io.sf.modules.config.service;

import io.sf.modules.config.entity.ConfigSetting;

import java.util.List;
import java.util.Optional;

public interface IConfigService {
    Optional<ConfigSetting> getGlobal(String key);
    Optional<ConfigSetting> getForTenant(Long tenantId, String key);
    Optional<ConfigSetting> getForMerchant(Long merchantId, String key);

    List<ConfigSetting> listTenantConfigs(Long tenantId);
    List<ConfigSetting> listMerchantConfigs(Long merchantId);

    ConfigSetting upsertGlobal(String key, String value, String valueType, String description, Boolean enabled);
    ConfigSetting upsertTenant(Long tenantId, String key, String value, String valueType, String description, Boolean enabled);
    ConfigSetting upsertMerchant(Long merchantId, String key, String value, String valueType, String description, Boolean enabled);
}