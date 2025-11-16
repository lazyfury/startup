package io.sf.modules.config.repository;

import io.sf.modules.config.entity.ConfigSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfigSettingRepository extends JpaRepository<ConfigSetting, Long> {

    // 全局
    Optional<ConfigSetting> findByTenantIdIsNullAndMerchantIdIsNullAndKey(String key);

    // 租户级
    Optional<ConfigSetting> findByTenantIdAndMerchantIdIsNullAndKey(Long tenantId, String key);
    List<ConfigSetting> findAllByTenantIdAndMerchantIdIsNull(Long tenantId);

    // 商户级
    Optional<ConfigSetting> findByMerchantIdAndKey(Long merchantId, String key);
    List<ConfigSetting> findAllByMerchantId(Long merchantId);
}