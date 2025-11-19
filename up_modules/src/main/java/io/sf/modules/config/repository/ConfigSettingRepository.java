package io.sf.modules.config.repository;

import io.sf.modules.config.entity.ConfigSetting;
import io.sf.modules.config.entity.ScopeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfigSettingRepository extends JpaRepository<ConfigSetting, Long> {
    Optional<ConfigSetting> findByKeyAndScopeTypeAndStatusIsTrue(String key, ScopeType scopeType);
    Optional<ConfigSetting> findByKeyAndScopeTypeAndScopeIdAndStatusIsTrue(String key, ScopeType scopeType, Long scopeId);
}