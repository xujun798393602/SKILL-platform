package com.skill.platform.graph.service;

import com.skill.platform.common.exception.BusinessException;
import com.skill.platform.common.exception.ErrorCode;
import com.skill.platform.core.model.Skill;
import com.skill.platform.core.repository.SkillRepository;
import com.skill.platform.graph.model.SkillRelation;
import com.skill.platform.graph.model.dto.RelationRequest;
import com.skill.platform.graph.model.dto.RelationResponse;
import com.skill.platform.graph.repository.SkillRelationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Service for SKILL graph operations (F016 - SKILL图谱服务).
 * <p>
 * Handles relation queries with BFS/DFS traversal up to a configurable max depth,
 * relation creation with circular dependency detection, and full graph visualization data.
 */
@Service
@Slf4j
public class GraphService {

    private final SkillRelationRepository skillRelationRepository;
    private final SkillRepository skillRepository;

    public GraphService(SkillRelationRepository skillRelationRepository,
                        SkillRepository skillRepository) {
        this.skillRelationRepository = skillRelationRepository;
        this.skillRepository = skillRepository;
    }

    /**
     * Get relations for a SKILL as a tree structure up to maxDepth.
     * <p>
     * Traverses outgoing relations using BFS, building a tree of related skills.
     * Each node contains the relation metadata and its children (deeper relations).
     *
     * @param skillId  the root SKILL ID
     * @param maxDepth maximum traversal depth (default 3)
     * @return list of root-level relation responses with nested children
     */
    @Transactional(readOnly = true)
    public List<RelationResponse> getRelations(UUID skillId, int maxDepth) {
        if (!skillRepository.existsById(skillId)) {
            throw new BusinessException(ErrorCode.SYSTEM001);
        }

        Set<UUID> visited = new HashSet<>();
        visited.add(skillId);
        return buildRelationTree(skillId, maxDepth, 0, visited);
    }

    /**
     * Create a new relation between two SKILLs.
     * <p>
     * Validates:
     * <ol>
     *   <li>Both source and target SKILLs exist.</li>
     *   <li>The relation does not already exist (unique constraint on source, target, type).</li>
     *   <li>No circular dependency: target must not be able to reach source via existing relations.</li>
     * </ol>
     *
     * @param sourceSkillId the source SKILL ID
     * @param request       the relation creation request
     * @return the created relation response
     */
    @Transactional
    public RelationResponse createRelation(UUID sourceSkillId, RelationRequest request) {
        // 1. Validate source skill exists
        Skill sourceSkill = skillRepository.findById(sourceSkillId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SYSTEM001));

        // 2. Validate target skill exists
        Skill targetSkill = skillRepository.findById(request.getTargetSkillId())
                .orElseThrow(() -> new BusinessException(ErrorCode.SYSTEM001));

        // 3. Prevent self-referencing relations
        if (sourceSkillId.equals(request.getTargetSkillId())) {
            throw new BusinessException("GRAPH003",
                    "A SKILL cannot have a relation with itself", 400);
        }

        // 4. Check for duplicate relation
        Optional<SkillRelation> existing = skillRelationRepository
                .findBySourceSkillIdAndTargetSkillIdAndRelationType(
                        sourceSkillId, request.getTargetSkillId(), request.getRelationType());
        if (existing.isPresent()) {
            throw new BusinessException("GRAPH004",
                    "Relation already exists between these SKILLs with type: " + request.getRelationType(), 409);
        }

        // 5. Detect circular dependency: check if target can reach source
        if (hasPathTo(request.getTargetSkillId(), sourceSkillId)) {
            throw new BusinessException("GRAPH005",
                    "Circular dependency detected: adding this relation would create a cycle", 409);
        }

        // 6. Create and persist the relation
        SkillRelation relation = SkillRelation.builder()
                .sourceSkill(sourceSkill)
                .targetSkill(targetSkill)
                .relationType(request.getRelationType())
                .label(request.getLabel())
                .build();

        relation = skillRelationRepository.save(relation);
        log.info("Relation created: id={}, source={}, target={}, type={}",
                relation.getId(), sourceSkillId, request.getTargetSkillId(), request.getRelationType());

        return toResponse(relation);
    }

    /**
     * Get full graph visualization data for a SKILL.
     * <p>
     * Returns both incoming and outgoing relations as a flat list,
     * suitable for rendering in a graph visualization component.
     *
     * @param skillId the SKILL ID
     * @return list of all relation responses (both directions)
     */
    @Transactional(readOnly = true)
    public List<RelationResponse> getFullGraph(UUID skillId) {
        if (!skillRepository.existsById(skillId)) {
            throw new BusinessException(ErrorCode.SYSTEM001);
        }

        List<SkillRelation> outgoing = skillRelationRepository.findBySourceSkillId(skillId);
        List<SkillRelation> incoming = skillRelationRepository.findByTargetSkillId(skillId);

        List<RelationResponse> result = new ArrayList<>();
        outgoing.forEach(r -> result.add(toResponse(r)));
        incoming.forEach(r -> result.add(toResponse(r)));

        log.debug("Full graph for skill {}: {} outgoing, {} incoming relations",
                skillId, outgoing.size(), incoming.size());

        return result;
    }

    /**
     * Check if there is a path from startId to targetId using BFS.
     * <p>
     * This is used for circular dependency detection: before creating A->B,
     * we check if B can reach A via existing outgoing relations.
     *
     * @param startId  the starting SKILL ID (B)
     * @param targetId the target SKILL ID (A)
     * @return true if a path exists from startId to targetId
     */
    private boolean hasPathTo(UUID startId, UUID targetId) {
        Set<UUID> visited = new HashSet<>();
        Queue<UUID> queue = new LinkedList<>();
        queue.add(startId);
        visited.add(startId);

        while (!queue.isEmpty()) {
            UUID current = queue.poll();
            List<SkillRelation> outgoing = skillRelationRepository.findBySourceSkillId(current);

            for (SkillRelation relation : outgoing) {
                UUID nextId = relation.getTargetSkill().getId();
                if (nextId.equals(targetId)) {
                    return true;
                }
                if (!visited.contains(nextId)) {
                    visited.add(nextId);
                    queue.add(nextId);
                }
            }
        }
        return false;
    }

    /**
     * Recursively build a relation tree from a given skill up to maxDepth.
     *
     * @param skillId  the current skill ID
     * @param maxDepth the maximum depth to traverse
     * @param current  the current depth level
     * @param visited  set of already visited skill IDs (cycle prevention)
     * @return list of relation responses at this level
     */
    private List<RelationResponse> buildRelationTree(UUID skillId, int maxDepth, int current,
                                                      Set<UUID> visited) {
        if (current >= maxDepth) {
            return Collections.emptyList();
        }

        List<SkillRelation> relations = skillRelationRepository.findBySourceSkillId(skillId);
        List<RelationResponse> result = new ArrayList<>();

        for (SkillRelation relation : relations) {
            UUID targetId = relation.getTargetSkill().getId();
            if (visited.contains(targetId)) {
                // Skip already-visited nodes to prevent infinite loops
                continue;
            }
            visited.add(targetId);

            RelationResponse response = toResponse(relation);
            response.setChildren(buildRelationTree(targetId, maxDepth, current + 1, visited));
            result.add(response);
        }

        return result;
    }

    /**
     * Convert a SkillRelation entity to a RelationResponse DTO.
     *
     * @param relation the SkillRelation entity
     * @return the RelationResponse DTO
     */
    private RelationResponse toResponse(SkillRelation relation) {
        return RelationResponse.builder()
                .id(relation.getId())
                .sourceSkillId(relation.getSourceSkill().getId())
                .sourceSkillName(relation.getSourceSkill().getName())
                .targetSkillId(relation.getTargetSkill().getId())
                .targetSkillName(relation.getTargetSkill().getName())
                .relationType(relation.getRelationType())
                .label(relation.getLabel())
                .createdAt(relation.getCreatedAt())
                .build();
    }
}
