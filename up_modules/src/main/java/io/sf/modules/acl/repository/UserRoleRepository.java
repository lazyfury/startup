package io.sf.modules.acl.repository;

import io.sf.modules.acl.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    List<UserRole> findAllByUserId(Long userId);
    void deleteAllByUserId(Long userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    void deleteByUserIdAndRoleId(Long userId, Long roleId);
}
