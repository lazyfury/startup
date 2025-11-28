package io.sf.modules.acl.repository;

import io.sf.modules.acl.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    List<RolePermission> findAllByRoleId(Long roleId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from RolePermission where roleId = ?1")
    void deleteAllByRoleId(Long roleId);

    void deleteByRoleIdAndPermissionId(Long roleId, Long permissionId);
}
