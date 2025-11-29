package io.sf.modules.acl.repository;

import io.sf.modules.acl.entity.Permission;
import io.sf.modules.config.entity.ScopeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {
    boolean existsByCodeAndScopeTypeAndScopeId(String code, ScopeType scopeType, Long scopeId);
    java.util.Optional<Permission> findByCodeAndScopeTypeAndScopeId(String code, ScopeType scopeType, Long scopeId);
    List<Permission> findAllByScopeTypeAndScopeId(ScopeType scopeType, Long scopeId);
    List<Permission> findAllByCodeStartingWithAndScopeTypeAndScopeId(String codePrefix, ScopeType scopeType, Long scopeId);
    List<Permission> findAllByCodeStartingWith(String codePrefix);

    List<Permission> findAllByTagStartingWithIgnoreCase(String tagPrefix);
    List<Permission> findAllByTagStartingWithIgnoreCaseAndScopeTypeAndScopeId(String tagPrefix, ScopeType scopeType, Long scopeId);

    @Query("select distinct p.tag from Permission p where p.tag is not null and p.tag <> ''")
    List<String> findDistinctTags();

    @Query("select distinct p.tag from Permission p where p.tag is not null and p.tag <> '' and p.scopeType = ?1 and (p.scopeId = ?2 or (?2 is null and p.scopeId is null))")
    List<String> findDistinctTagsByScope(ScopeType scopeType, Long scopeId);
}
