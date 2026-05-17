package com.skill.platform.suite.service;

import com.skill.platform.auth.model.User;
import com.skill.platform.auth.repository.UserRepository;
import com.skill.platform.common.exception.BusinessException;
import com.skill.platform.common.response.PageResponse;
import com.skill.platform.common.util.UserContext;
import com.skill.platform.core.model.Skill;
import com.skill.platform.core.repository.SkillRepository;
import com.skill.platform.graph.model.SkillRelation;
import com.skill.platform.graph.repository.SkillRelationRepository;
import com.skill.platform.suite.model.Suite;
import com.skill.platform.suite.model.SuiteSkill;
import com.skill.platform.suite.model.dto.SuiteRequest;
import com.skill.platform.suite.model.dto.SuiteResponse;
import com.skill.platform.suite.repository.SuiteRepository;
import com.skill.platform.suite.repository.SuiteSkillRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for managing SKILL suites.
 * <p>
 * Handles CRUD operations, skill association, and circular-dependency
 * validation before deployment.
 */
@Service
@Slf4j
public class SuiteService {

    private final SuiteRepository suiteRepository;
    private final SuiteSkillRepository suiteSkillRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final SkillRelationRepository skillRelationRepository;

    public SuiteService(SuiteRepository suiteRepository,
                        SuiteSkillRepository suiteSkillRepository,
                        SkillRepository skillRepository,
                        UserRepository userRepository,
                        SkillRelationRepository skillRelationRepository) {
        this.suiteRepository = suiteRepository;
        this.suiteSkillRepository = suiteSkillRepository;
        this.skillRepository = skillRepository;
        this.userRepository = userRepository;
        this.skillRelationRepository = skillRelationRepository;
    }

    private static final int MIN_SKILLS = 2;
    private static final int MAX_SKILLS = 20;

    /**
     * Create a new suite with the given skills.
     *
     * @param request the suite creation request
     * @return the created suite response
     */
    @Transactional
    public SuiteResponse createSuite(SuiteRequest request) {
        validateSkillIds(request.getSkillIds());

        String userId = UserContext.getUserId();
        User owner = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new BusinessException("AUTH001", "User not found", 401));

        Suite suite = Suite.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .visibility(request.getVisibility())
                .status("draft")
                .owner(owner)
                .build();
        suite = suiteRepository.save(suite);

        List<SuiteSkill> suiteSkills = createSuiteSkills(suite, request.getSkillIds());
        suiteSkillRepository.saveAll(suiteSkills);

        log.info("Suite created: id={}, name={}, skillCount={}", suite.getId(), suite.getName(), suiteSkills.size());
        return buildSuiteResponse(suite, suiteSkills);
    }

    /**
     * Get a suite by ID with its associated skills.
     *
     * @param id the suite ID
     * @return the suite response
     */
    @Transactional(readOnly = true)
    public SuiteResponse getSuite(UUID id) {
        Suite suite = findSuiteOrThrow(id);
        List<SuiteSkill> suiteSkills = suiteSkillRepository.findBySuiteIdOrderBySortOrder(id);
        return buildSuiteResponse(suite, suiteSkills);
    }

    /**
     * List suites with pagination. If ownerId is provided, filters by owner;
     * otherwise returns all suites.
     *
     * @param ownerId  optional owner filter
     * @param status   optional status filter
     * @param page     page number (1-based)
     * @param pageSize items per page
     * @return paginated suite list
     */
    @Transactional(readOnly = true)
    public PageResponse<SuiteResponse> listSuites(UUID ownerId, String status, int page, int pageSize) {
        com.skill.platform.common.util.PageRequest pageRequest = com.skill.platform.common.util.PageRequest.builder()
                .page(page)
                .pageSize(pageSize)
                .build();

        Page<Suite> suitePage;
        if (ownerId != null) {
            suitePage = suiteRepository.findByOwnerId(ownerId, pageRequest.toPageable());
        } else if (status != null) {
            suitePage = suiteRepository.findByStatus(status, pageRequest.toPageable());
        } else {
            suitePage = suiteRepository.findAll(pageRequest.toPageable());
        }

        List<SuiteResponse> responses = suitePage.getContent().stream()
                .map(suite -> {
                    List<SuiteSkill> skills = suiteSkillRepository.findBySuiteIdOrderBySortOrder(suite.getId());
                    return buildSuiteResponse(suite, skills);
                })
                .collect(Collectors.toList());

        return PageResponse.of(suitePage.getTotalElements(), page, pageSize, responses);
    }

    /**
     * Update suite metadata (name, description, category, visibility).
     *
     * @param id      the suite ID
     * @param request the update request
     * @return the updated suite response
     */
    @Transactional
    public SuiteResponse updateSuite(UUID id, SuiteRequest request) {
        Suite suite = findSuiteOrThrow(id);
        assertOwnerOrAdmin(suite);

        suite.setName(request.getName());
        suite.setDescription(request.getDescription());
        suite.setCategory(request.getCategory());
        suite.setVisibility(request.getVisibility());
        suite = suiteRepository.save(suite);

        // Replace skills if provided
        if (request.getSkillIds() != null && !request.getSkillIds().isEmpty()) {
            validateSkillIds(request.getSkillIds());
            suiteSkillRepository.deleteBySuiteId(id);
            List<SuiteSkill> newSkills = createSuiteSkills(suite, request.getSkillIds());
            suiteSkillRepository.saveAll(newSkills);
            log.info("Suite skills replaced: suiteId={}, newCount={}", id, newSkills.size());
        }

        List<SuiteSkill> suiteSkills = suiteSkillRepository.findBySuiteIdOrderBySortOrder(id);
        return buildSuiteResponse(suite, suiteSkills);
    }

    /**
     * Delete a suite and its skill associations.
     *
     * @param id the suite ID
     */
    @Transactional
    public void deleteSuite(UUID id) {
        Suite suite = findSuiteOrThrow(id);
        assertOwnerOrAdmin(suite);

        suiteSkillRepository.deleteBySuiteId(id);
        suiteRepository.delete(suite);
        log.info("Suite deleted: id={}", id);
    }

    /**
     * Deploy a suite after validating that no circular dependencies exist
     * among its skills.
     *
     * @param id the suite ID
     * @return the updated suite response with status "deployed"
     */
    @Transactional
    public SuiteResponse deploySuite(UUID id) {
        Suite suite = findSuiteOrThrow(id);
        assertOwnerOrAdmin(suite);

        List<SuiteSkill> suiteSkills = suiteSkillRepository.findBySuiteIdOrderBySortOrder(id);
        if (suiteSkills.size() < MIN_SKILLS) {
            throw new BusinessException("SUITE001",
                    "A suite must contain at least " + MIN_SKILLS + " skills to deploy", 400);
        }

        List<UUID> skillIds = suiteSkills.stream()
                .map(ss -> ss.getSkill().getId())
                .collect(Collectors.toList());

        checkCircularDependencies(skillIds);

        suite.setStatus("deployed");
        suite = suiteRepository.save(suite);

        log.info("Suite deployed: id={}, name={}", suite.getId(), suite.getName());
        return buildSuiteResponse(suite, suiteSkills);
    }

    // ---- internal helpers ----

    private Suite findSuiteOrThrow(UUID id) {
        return suiteRepository.findById(id)
                .orElseThrow(() -> new BusinessException("SUITE001", "Suite not found: " + id, 404));
    }

    private void assertOwnerOrAdmin(Suite suite) {
        String userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException("AUTH001", "Not authenticated", 401);
        }
        boolean isOwner = suite.getOwner().getId().toString().equals(userId);
        boolean isAdmin = UserContext.hasRole("ADMIN");
        if (!isOwner && !isAdmin) {
            throw new BusinessException("AUTH003", "Insufficient permissions", 403);
        }
    }

    private void validateSkillIds(List<UUID> skillIds) {
        if (skillIds == null || skillIds.size() < MIN_SKILLS) {
            throw new BusinessException("VALIDATION001",
                    "A suite must contain at least " + MIN_SKILLS + " skills", 400);
        }
        if (skillIds.size() > MAX_SKILLS) {
            throw new BusinessException("VALIDATION001",
                    "A suite must not contain more than " + MAX_SKILLS + " skills", 400);
        }

        // Verify all skill IDs exist
        for (UUID skillId : skillIds) {
            if (!skillRepository.existsById(skillId)) {
                throw new BusinessException("SYSTEM001", "Skill not found: " + skillId, 404);
            }
        }
    }

    private List<SuiteSkill> createSuiteSkills(Suite suite, List<UUID> skillIds) {
        List<SuiteSkill> result = new ArrayList<>();
        for (int i = 0; i < skillIds.size(); i++) {
            Skill skill = skillRepository.findById(skillIds.get(i))
                    .orElseThrow(() -> new BusinessException("SYSTEM001", "Skill not found: " + skillIds.get(i), 404));
            result.add(SuiteSkill.builder()
                    .suite(suite)
                    .skill(skill)
                    .sortOrder(i + 1)
                    .build());
        }
        return result;
    }

    /**
     * Check for circular dependencies among the given skills.
     * <p>
     * Builds a directed dependency graph from {@link SkillRelation} entries
     * where the relation type is "depends_on", restricted to skills in the
     * suite. Uses DFS to detect cycles.
     *
     * @param skillIds the skill IDs in the suite
     * @throws BusinessException if a circular dependency is detected
     */
    private void checkCircularDependencies(List<UUID> skillIds) {
        Set<UUID> skillIdSet = new HashSet<>(skillIds);

        // Build adjacency list from depends_on relations within the suite
        Map<UUID, List<UUID>> adjacency = new HashMap<>();
        for (UUID skillId : skillIds) {
            adjacency.put(skillId, new ArrayList<>());
        }

        // Fetch all relations for all skills in the suite
        for (UUID skillId : skillIds) {
            List<SkillRelation> sourceRelations = skillRelationRepository.findBySourceSkillId(skillId);
            for (SkillRelation rel : sourceRelations) {
                UUID targetId = rel.getTargetSkill().getId();
                if ("depends_on".equals(rel.getRelationType()) && skillIdSet.contains(targetId)) {
                    adjacency.get(skillId).add(targetId);
                }
            }
        }

        // DFS cycle detection
        Set<UUID> visited = new HashSet<>();
        Set<UUID> inStack = new HashSet<>();

        for (UUID skillId : skillIds) {
            if (hasCycle(skillId, adjacency, visited, inStack)) {
                throw new BusinessException("SUITE002",
                        "Circular dependency detected among suite skills", 400);
            }
        }
    }

    private boolean hasCycle(UUID node, Map<UUID, List<UUID>> adjacency,
                             Set<UUID> visited, Set<UUID> inStack) {
        if (inStack.contains(node)) {
            return true;
        }
        if (visited.contains(node)) {
            return false;
        }

        visited.add(node);
        inStack.add(node);

        List<UUID> neighbors = adjacency.getOrDefault(node, List.of());
        for (UUID neighbor : neighbors) {
            if (hasCycle(neighbor, adjacency, visited, inStack)) {
                return true;
            }
        }

        inStack.remove(node);
        return false;
    }

    private SuiteResponse buildSuiteResponse(Suite suite, List<SuiteSkill> suiteSkills) {
        List<SuiteResponse.SuiteSkillItem> skillItems = suiteSkills.stream()
                .map(ss -> SuiteResponse.SuiteSkillItem.builder()
                        .skillId(ss.getSkill().getId())
                        .skillName(ss.getSkill().getName())
                        .skillType(ss.getSkill().getSkillType())
                        .sortOrder(ss.getSortOrder())
                        .build())
                .collect(Collectors.toList());

        return SuiteResponse.builder()
                .id(suite.getId())
                .name(suite.getName())
                .description(suite.getDescription())
                .category(suite.getCategory())
                .visibility(suite.getVisibility())
                .status(suite.getStatus())
                .ownerName(suite.getOwner().getName())
                .createdAt(suite.getCreatedAt())
                .updatedAt(suite.getUpdatedAt())
                .skills(skillItems)
                .build();
    }
}
