package com.skill.platform.auth.repository;

import com.skill.platform.auth.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {
    List<UserRole> findByUserId(UUID userId);
    List<UserRole> findByRoleId(UUID roleId);
    @Transactional
    void deleteByUserIdAndRoleId(UUID userId, UUID roleId);
}
