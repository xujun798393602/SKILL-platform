package com.skill.platform.admin.repository;

import com.skill.platform.admin.model.HelpDoc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface HelpDocRepository extends JpaRepository<HelpDoc, UUID> {
    Page<HelpDoc> findByDocType(String docType, Pageable pageable);
    Page<HelpDoc> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);
}
