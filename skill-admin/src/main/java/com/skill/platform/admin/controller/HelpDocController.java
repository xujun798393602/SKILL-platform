package com.skill.platform.admin.controller;

import com.skill.platform.admin.model.dto.HelpDocRequest;
import com.skill.platform.admin.model.dto.HelpDocResponse;
import com.skill.platform.admin.service.HelpDocService;
import com.skill.platform.common.exception.BusinessException;
import com.skill.platform.common.exception.ErrorCode;
import com.skill.platform.common.response.ApiResponse;
import com.skill.platform.common.response.PageResponse;
import com.skill.platform.common.util.PageRequest;
import com.skill.platform.common.util.UserContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for help document management.
 * <p>
 * Read endpoints are open to all authenticated users.
 * Write endpoints (create, update, delete) require the ADMIN role.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/help-docs")
@RequiredArgsConstructor
public class HelpDocController {

    private final HelpDocService helpDocService;

    /**
     * List help documents with optional docType filter and pagination.
     *
     * @param docType  optional doc type filter
     * @param page     page number (default 1)
     * @param pageSize page size (default 20)
     * @return paginated help doc responses
     */
    @GetMapping
    public ApiResponse<PageResponse<HelpDocResponse>> list(
            @RequestParam(required = false) String docType,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {

        PageRequest pageRequest = PageRequest.builder()
                .page(page)
                .pageSize(pageSize)
                .sortBy("sortOrder")
                .sortOrder("asc")
                .build();

        PageResponse<HelpDocResponse> result = helpDocService.list(docType, pageRequest);
        return ApiResponse.success(result);
    }

    /**
     * Get a help document by its ID.
     *
     * @param id the document ID
     * @return the help doc response
     */
    @GetMapping("/{id}")
    public ApiResponse<HelpDocResponse> getById(@PathVariable UUID id) {
        HelpDocResponse doc = helpDocService.getById(id);
        return ApiResponse.success(doc);
    }

    /**
     * Search help documents by keyword in title.
     *
     * @param keyword  the search keyword
     * @param page     page number (default 1)
     * @param pageSize page size (default 20)
     * @return paginated help doc responses matching the keyword
     */
    @GetMapping("/search")
    public ApiResponse<PageResponse<HelpDocResponse>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {

        PageRequest pageRequest = PageRequest.builder()
                .page(page)
                .pageSize(pageSize)
                .sortBy("sortOrder")
                .sortOrder("asc")
                .build();

        PageResponse<HelpDocResponse> result = helpDocService.search(keyword, pageRequest);
        return ApiResponse.success(result);
    }

    /**
     * Create a new help document. Admin only.
     *
     * @param request the create request
     * @return the created help doc response
     */
    @PostMapping
    public ApiResponse<HelpDocResponse> create(@Valid @RequestBody HelpDocRequest request) {
        requireAdmin();
        HelpDocResponse doc = helpDocService.create(request);
        return ApiResponse.success("Help document created successfully", doc);
    }

    /**
     * Update an existing help document. Admin only.
     *
     * @param id      the document ID to update
     * @param request the update request
     * @return the updated help doc response
     */
    @PutMapping("/{id}")
    public ApiResponse<HelpDocResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody HelpDocRequest request) {
        requireAdmin();
        HelpDocResponse doc = helpDocService.update(id, request);
        return ApiResponse.success("Help document updated successfully", doc);
    }

    /**
     * Delete a help document. Admin only.
     *
     * @param id the document ID to delete
     * @return success response
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        requireAdmin();
        helpDocService.delete(id);
        return ApiResponse.success("Help document deleted successfully");
    }

    /**
     * Verify that the current user has the ADMIN role.
     *
     * @throws BusinessException if the user does not have the ADMIN role
     */
    private void requireAdmin() {
        if (!UserContext.hasRole("ADMIN")) {
            throw new BusinessException(ErrorCode.AUTH003);
        }
    }
}
