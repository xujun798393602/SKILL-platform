package com.skill.platform.suite.repository;

import com.skill.platform.suite.model.SuiteSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SuiteSkillRepository extends JpaRepository<SuiteSkill, UUID> {
    List<SuiteSkill> findBySuiteIdOrderBySortOrder(UUID suiteId);
    void deleteBySuiteId(UUID suiteId);
    long countBySuiteId(UUID suiteId);
}
