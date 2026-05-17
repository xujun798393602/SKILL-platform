package com.skill.platform.graph.controller;

import com.skill.platform.common.response.ApiResponse;
import com.skill.platform.graph.model.dto.RelationRequest;
import com.skill.platform.graph.model.dto.RelationResponse;
import com.skill.platform.graph.service.GraphService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for SKILL graph endpoints (F016 - SKILL图谱服务).
 * <p>
 * Provides endpoints for querying SKILL relation trees, creating new relations
 * with circular dependency detection, and retrieving full graph visualization data.
 */
@RestController
@RequestMapping("/api/v1")
@Slf4j
public class GraphController {

    private final GraphService graphService;

    public GraphController(GraphService graphService) {
        this.graphService = graphService;
    }

    /**
     * Get relations for a SKILL as a tree structure up to maxDepth.
     *
     * @param skillId  the root SKILL ID
     * @param maxDepth maximum traversal depth (default 3)
     * @return tree of related skills
     */
    @GetMapping("/skills/{skillId}/relations")
    public ApiResponse<List<RelationResponse>> getRelations(
            @PathVariable UUID skillId,
            @RequestParam(defaultValue = "3") int maxDepth) {

        log.info("GET /api/v1/skills/{}/relations - maxDepth={}", skillId, maxDepth);

        List<RelationResponse> relations = graphService.getRelations(skillId, maxDepth);
        return ApiResponse.success(relations);
    }

    /**
     * Create a new relation for a SKILL.
     * <p>
     * Validates that no circular dependency would be created.
     *
     * @param skillId the source SKILL ID
     * @param request the relation creation request
     * @return the created relation
     */
    @PostMapping("/skills/{skillId}/relations")
    public ApiResponse<RelationResponse> createRelation(
            @PathVariable UUID skillId,
            @Valid @RequestBody RelationRequest request) {

        log.info("POST /api/v1/skills/{}/relations - targetSkillId={}, relationType={}",
                skillId, request.getTargetSkillId(), request.getRelationType());

        RelationResponse response = graphService.createRelation(skillId, request);
        return ApiResponse.success("Relation created successfully", response);
    }

    /**
     * Get full graph visualization data for a SKILL.
     * <p>
     * Returns both incoming and outgoing relations as a flat list.
     *
     * @param skillId the SKILL ID
     * @return all relations involving this SKILL
     */
    @GetMapping("/skills/{skillId}/graph")
    public ApiResponse<List<RelationResponse>> getFullGraph(@PathVariable UUID skillId) {

        log.info("GET /api/v1/skills/{}/graph", skillId);

        List<RelationResponse> graph = graphService.getFullGraph(skillId);
        return ApiResponse.success(graph);
    }
}
