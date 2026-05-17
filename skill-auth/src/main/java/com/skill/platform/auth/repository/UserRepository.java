package com.skill.platform.auth.repository;

import com.skill.platform.auth.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmployeeId(String employeeId);
    Optional<User> findByEmail(String email);
    boolean existsByEmployeeId(String employeeId);
    boolean existsByEmail(String email);
    Page<User> findByStatus(String status, Pageable pageable);
}
