package com.skill.platform.suite.controller;

import com.skill.platform.common.response.ApiResponse;
import com.skill.platform.common.response.PageResponse;
import com.skill.platform.suite.model.dto.SuiteRequest;
import com.skill.platform.suite.model.dto.SuiteResponse;
import com.skill.platform.suite.service.SuiteService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST controller for SKILL suite management endpoints.
 */
@RestController
@RequestMapping("/api/v1/suites")
@Slf4j
public class SuiteController {

    private final SuiteService suiteService;

    public SuiteController(SuiteService suiteService) {
        this.suiteService = suiteService;
    }

    /**
     * Create a new suite with the provided skills.
     *
     * @param request the suite creation request
     * @return the created suite
     */
    @PostMapping
    public ApiResponse<SuiteResponse> createSuite(@Valid @RequestBody SuiteRequest request) {
        log.info("Creating suite: name={}, skillCount={}", request.getName(), request.getSkillIds().size());
        return ApiResponse.success(suiteService.createSuite(request));
    }

    /**
     * Get a suite by ID with its associated skills.
     *
     * @param id the suite ID
     * @return the suite details
     */
    @GetMapping("/{id}")
    public ApiResponse<SuiteResponse> getSuite(@PathVariable UUID id) {
        return ApiResponse.success(suiteService.getSuite(id));
    }

    /**
     * List suites with optional filtering and pagination.
     *
     * @param ownerId  optional owner ID filter
     * @param status   optional status filter
     * @param page     page number (default 1)
     * @param pageSize items per page (default 20)
     * @return paginated list of suites
     */
    @GetMapping
    public ApiResponse<PageResponse<SuiteResponse>> listSuites(
            @RequestParam(required = false) UUID ownerId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ApiResponse.success(suiteService.listSuites(ownerId, status, page, pageSize));
    }

    /**
     * Update suite metadata and optionally replace its skills.
     *
     * @param id      the suite ID
     * @param request the update request
     * @return the updated suite
     */
    @PutMapping("/{id}")
    public ApiResponse<SuiteResponse> updateSuite(@PathVariable UUID id,
                                                   @Valid @RequestBody SuiteRequest request) {
        log.info("Updating suite: id={}", id);
        return ApiResponse.success(suiteService.updateSuite(id, request));
    }

    /**
     * Delete a suite and its skill associations.
     *
     * @param id the suite ID
     * @return success response
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteSuite(@PathVariable UUID id) {
        log.info("Deleting suite: id={}", id);
        suiteService.deleteSuite(id);
        return ApiResponse.success("Suite deleted successfully", null);
    }

    /**
     * Deploy a suite after validating no circular dependencies exist.
     *
     * @param id the suite ID
     * @return the deployed suite
     */
    @PostMapping("/{id}/deploy")
    public ApiResponse<SuiteResponse> deploySuite(@PathVariable UUID id) {
        log.info("Deploying suite: id={}", id);
        return ApiResponse.success(suiteService.deploySuite(id));
    }
}
