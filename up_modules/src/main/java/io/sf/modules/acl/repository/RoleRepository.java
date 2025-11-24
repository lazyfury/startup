package io.sf.modules.acl.repository;

import io.sf.modules.acl.entity.Role;
import io.sf.modules.config.entity.ScopeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {
    boolean existsByCodeAndScopeTypeAndScopeId(String code, ScopeType scopeType, Long scopeId);
    List<Role> findAllByScopeTypeAndScopeId(ScopeType scopeType, Long scopeId);
}

