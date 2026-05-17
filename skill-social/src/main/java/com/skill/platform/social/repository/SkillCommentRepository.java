package com.skill.platform.social.repository;

import com.skill.platform.social.model.SkillComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SkillCommentRepository extends JpaRepository<SkillComment, UUID> {

    Page<SkillComment> findBySkillIdAndParentIsNullAndIsDeletedFalse(UUID skillId, Pageable pageable);

    List<SkillComment> findByParentIdAndIsDeletedFalse(UUID parentId);

    long countBySkillIdAndIsDeletedFalse(UUID skillId);
}
