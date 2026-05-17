package com.skill.platform.admin.service;

import com.skill.platform.admin.model.HelpDoc;
import com.skill.platform.admin.model.dto.HelpDocRequest;
import com.skill.platform.admin.model.dto.HelpDocResponse;
import com.skill.platform.admin.repository.HelpDocRepository;
import com.skill.platform.common.exception.BusinessException;
import com.skill.platform.common.exception.ErrorCode;
import com.skill.platform.common.response.PageResponse;
import com.skill.platform.common.util.PageRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service for managing help documents.
 * <p>
 * Provides CRUD operations and keyword search for help documents.
 * Create, update, and delete operations are restricted to admin users
 * (enforced at the controller layer).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HelpDocService {

    private final HelpDocRepository helpDocRepository;

    /**
     * List help documents with optional docType filter and pagination.
     *
     * @param docType    optional doc type filter
     * @param pageRequest pagination parameters
     * @return paginated help doc responses
     */
    @Transactional(readOnly = true)
    public PageResponse<HelpDocResponse> list(String docType, PageRequest pageRequest) {
        var page = (docType != null && !docType.isBlank())
                ? helpDocRepository.findByDocType(docType, pageRequest.toPageable())
                : helpDocRepository.findAll(pageRequest.toPageable());

        var responses = page.getContent().stream()
                .map(HelpDocResponse::fromEntity)
                .toList();

        return PageResponse.of(page.getTotalElements(), page.getNumber() + 1,
                page.getSize(), responses);
    }

    /**
     * Get a help document by its ID.
     *
     * @param id the document ID
     * @return the help doc response
     * @throws BusinessException if the document is not found
     */
    @Transactional(readOnly = true)
    public HelpDocResponse getById(UUID id) {
        HelpDoc doc = findDocById(id);
        return HelpDocResponse.fromEntity(doc);
    }

    /**
     * Search help documents by keyword in title.
     *
     * @param keyword     the search keyword
     * @param pageRequest pagination parameters
     * @return paginated help doc responses matching the keyword
     * @throws BusinessException if keyword is blank
     */
    @Transactional(readOnly = true)
    public PageResponse<HelpDocResponse> search(String keyword, PageRequest pageRequest) {
        if (keyword == null || keyword.isBlank()) {
            throw new BusinessException(ErrorCode.SEARCH001);
        }

        var page = helpDocRepository.findByTitleContainingIgnoreCase(keyword, pageRequest.toPageable());
        var responses = page.getContent().stream()
                .map(HelpDocResponse::fromEntity)
                .toList();

        return PageResponse.of(page.getTotalElements(), page.getNumber() + 1,
                page.getSize(), responses);
    }

    /**
     * Create a new help document.
     *
     * @param request the create request
     * @return the created help doc response
     */
    @Transactional
    public HelpDocResponse create(HelpDocRequest request) {
        HelpDoc doc = HelpDoc.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .docType(request.getDocType())
                .category(request.getCategory())
                .sortOrder(request.getSortOrder())
                .build();

        HelpDoc saved = helpDocRepository.save(doc);
        log.info("Help document created: id={}, title='{}'", saved.getId(), saved.getTitle());
        return HelpDocResponse.fromEntity(saved);
    }

    /**
     * Update an existing help document.
     *
     * @param id      the document ID to update
     * @param request the update request
     * @return the updated help doc response
     * @throws BusinessException if the document is not found
     */
    @Transactional
    public HelpDocResponse update(UUID id, HelpDocRequest request) {
        HelpDoc doc = findDocById(id);

        doc.setTitle(request.getTitle());
        doc.setContent(request.getContent());
        doc.setDocType(request.getDocType());
        doc.setCategory(request.getCategory());
        doc.setSortOrder(request.getSortOrder());

        HelpDoc saved = helpDocRepository.save(doc);
        log.info("Help document updated: id={}, title='{}'", saved.getId(), saved.getTitle());
        return HelpDocResponse.fromEntity(saved);
    }

    /**
     * Delete a help document by its ID.
     *
     * @param id the document ID to delete
     * @throws BusinessException if the document is not found
     */
    @Transactional
    public void delete(UUID id) {
        HelpDoc doc = findDocById(id);
        helpDocRepository.delete(doc);
        log.info("Help document deleted: id={}", id);
    }

    /**
     * Find a help doc by ID or throw if not found.
     *
     * @param id the document ID
     * @return the HelpDoc entity
     * @throws BusinessException if not found
     */
    private HelpDoc findDocById(UUID id) {
        return helpDocRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.HELPDOC001));
    }
}
