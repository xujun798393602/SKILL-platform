package com.skill.platform.core.service.query;

import com.skill.platform.core.model.Skill;
import com.skill.platform.core.model.SkillTag;
import com.skill.platform.core.model.Tag;
import com.skill.platform.core.repository.SkillRepository;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service for querying SKILL list with dynamic filtering.
 * <p>
 * Supports filtering by keyword (name/description), skill type, category,
 * status, and tags using JPA Specifications for composable query building.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class SkillListService {

    private final SkillRepository skillRepository;

    public SkillListService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    /**
     * Query skills with optional filters and pagination.
     *
     * @param keyword  optional keyword to match against name or description (LIKE)
     * @param type     optional exact skill type filter
     * @param category optional exact category filter
     * @param status   optional exact status filter
     * @param tags     optional list of tag names to filter by (OR semantics)
     * @param page     1-based page number
     * @param pageSize number of items per page
     * @return a Spring Data Page of matching Skill entities
     */
    public Page<Skill> listSkills(String keyword, String type, String category,
                                  String status, List<String> tags, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        Specification<Skill> spec = buildSpecification(keyword, type, category, status, tags);

        log.debug("Listing skills with filters: keyword={}, type={}, category={}, status={}, tags={}, page={}, pageSize={}",
                keyword, type, category, status, tags, page, pageSize);

        return skillRepository.findAll(spec, pageable);
    }

    /**
     * Build a JPA Specification from the given filter parameters.
     * All filters are combined with AND logic; multiple tags use OR logic.
     */
    private Specification<Skill> buildSpecification(String keyword, String type,
                                                     String category, String status,
                                                     List<String> tags) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Keyword filter: match against name or description (case-insensitive LIKE)
            if (keyword != null && !keyword.isBlank()) {
                String pattern = "%" + keyword.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), pattern),
                        cb.like(cb.lower(root.get("description")), pattern)
                ));
            }

            // Exact match filters
            if (type != null && !type.isBlank()) {
                predicates.add(cb.equal(root.get("skillType"), type));
            }

            if (category != null && !category.isBlank()) {
                predicates.add(cb.equal(root.get("category"), category));
            }

            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            // Tag filter: join through SkillTag to find skills with any of the specified tags
            if (tags != null && !tags.isEmpty()) {
                Subquery<UUID> tagSubquery = query.subquery(UUID.class);
                Root<SkillTag> skillTagRoot = tagSubquery.from(SkillTag.class);
                Join<SkillTag, Tag> tagJoin = skillTagRoot.join("tag");
                tagSubquery.select(skillTagRoot.get("skill").get("id"))
                        .where(tagJoin.get("name").in(tags));
                predicates.add(root.get("id").in(tagSubquery));
            }

            // Eagerly fetch the owner to avoid LazyInitializationException during serialization
            if (Long.class != query.getResultType()) {
                root.fetch("owner", JoinType.LEFT);
                query.distinct(true);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
