package io.sf.modules.config.repository;

import io.sf.modules.config.entity.ConfigSetting;
import io.sf.modules.config.entity.ScopeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface ConfigSettingRepository extends JpaRepository<ConfigSetting, Long>, JpaSpecificationExecutor<ConfigSetting> {
    Optional<ConfigSetting> findByKeyAndScopeTypeAndStatusIsTrue(String key, ScopeType scopeType);
    Optional<ConfigSetting> findByKeyAndScopeTypeAndScopeIdAndStatusIsTrue(String key, ScopeType scopeType, Long scopeId);
    List<ConfigSetting> findAllByGroupIdAndStatusIsTrue(Long groupId);
    long countByGroupIdAndStatusIsTrue(Long groupId);
}
