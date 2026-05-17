package com.skill.platform.core.controller;

import com.skill.platform.common.response.ApiResponse;
import com.skill.platform.common.response.PageResponse;
import com.skill.platform.core.model.Skill;
import com.skill.platform.core.service.query.SkillListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for SKILL list query endpoints.
 */
@RestController
@RequestMapping("/api/v1/skills")
@Slf4j
public class SkillListController {

    private final SkillListService skillListService;

    public SkillListController(SkillListService skillListService) {
        this.skillListService = skillListService;
    }

    /**
     * List skills with optional filtering and pagination.
     *
     * @param keyword  optional keyword to match against skill name or description
     * @param type     optional exact skill type filter
     * @param category optional exact category filter
     * @param status   optional exact status filter
     * @param tags     optional list of tag names to filter by
     * @param page     1-based page number (defaults to 1)
     * @param pageSize number of items per page (defaults to 20)
     * @return paginated list of skills matching the filters
     */
    @GetMapping
    public ApiResponse<PageResponse<Skill>> listSkills(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {

        log.info("GET /api/v1/skills - keyword={}, type={}, category={}, status={}, tags={}, page={}, pageSize={}",
                keyword, type, category, status, tags, page, pageSize);

        Page<Skill> skillPage = skillListService.listSkills(keyword, type, category, status, tags, page, pageSize);

        return ApiResponse.success(PageResponse.of(skillPage));
    }
}
