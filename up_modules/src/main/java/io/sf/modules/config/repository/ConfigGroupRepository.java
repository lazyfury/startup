package io.sf.modules.config.repository;

import io.sf.modules.config.entity.ConfigGroup;
import io.sf.modules.config.entity.ScopeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfigGroupRepository extends JpaRepository<ConfigGroup, Long>, JpaSpecificationExecutor<ConfigGroup> {
    Optional<ConfigGroup> findByCodeAndScopeTypeAndStatusIsTrue(String code, ScopeType scopeType);
    Optional<ConfigGroup> findByCodeAndScopeTypeAndScopeIdAndStatusIsTrue(String code, ScopeType scopeType, Long scopeId);
}

