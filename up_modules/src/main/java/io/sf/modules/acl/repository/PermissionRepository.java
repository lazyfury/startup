package io.sf.modules.acl.repository;

import io.sf.modules.acl.entity.Permission;
import io.sf.modules.config.entity.ScopeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {
    boolean existsByCodeAndScopeTypeAndScopeId(String code, ScopeType scopeType, Long scopeId);
    List<Permission> findAllByScopeTypeAndScopeId(ScopeType scopeType, Long scopeId);
}

