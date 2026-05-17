package com.skill.platform.core.controller;

import com.skill.platform.common.response.ApiResponse;
import com.skill.platform.common.response.PageResponse;
import com.skill.platform.core.model.dto.SkillSearchRequest;
import com.skill.platform.core.model.dto.SkillSearchResponse;
import com.skill.platform.core.service.search.SkillSearchService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for SKILL search endpoints.
 */
@RestController
@RequestMapping("/api/v1/skills")
@Slf4j
public class SkillSearchController {

    private final SkillSearchService skillSearchService;

    public SkillSearchController(SkillSearchService skillSearchService) {
        this.skillSearchService = skillSearchService;
    }

    /**
     * Full-text search across all published SKILLs.
     *
     * @param request the search request containing keyword, pagination and optional type filter
     * @return a paginated list of matching skills sorted by relevance
     */
    @PostMapping("/search")
    public ApiResponse<PageResponse<SkillSearchResponse>> searchSkills(
            @Valid @RequestBody SkillSearchRequest request) {
        log.info("Search request: keyword='{}', page={}, pageSize={}, skillType={}",
                request.getKeyword(), request.getPage(), request.getPageSize(), request.getSkillType());
        return ApiResponse.success(skillSearchService.search(request));
    }
}
