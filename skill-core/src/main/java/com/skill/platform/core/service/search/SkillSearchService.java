package com.skill.platform.core.service.search;

import com.skill.platform.common.exception.BusinessException;
import com.skill.platform.common.exception.ErrorCode;
import com.skill.platform.common.response.PageResponse;
import com.skill.platform.core.model.SkillIndexDocument;
import com.skill.platform.core.model.dto.SkillSearchRequest;
import com.skill.platform.core.model.dto.SkillSearchResponse;
import com.skill.platform.core.repository.SkillIndexRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Service for Elasticsearch-based full-text SKILL search.
 * <p>
 * Delegates to {@link SkillIndexRepository} which issues multi_match queries
 * against the {@code name}, {@code description} and {@code tags} fields,
 * boosted by relevance (name^3, tags^2, description).
 */
@Service
@Slf4j
public class SkillSearchService {

    private final SkillIndexRepository indexRepository;

    public SkillSearchService(SkillIndexRepository indexRepository) {
        this.indexRepository = indexRepository;
    }

    /**
     * Perform a full-text search over the SKILL index.
     *
     * @param request the search request containing keyword, pagination and optional type filter
     * @return a paginated list of matching skills sorted by relevance
     * @throws BusinessException if the keyword is blank
     */
    public PageResponse<SkillSearchResponse> search(SkillSearchRequest request) {
        validateRequest(request);

        // Convert 1-based page to 0-based for Spring Data
        // Use unsorted Pageable -- the ES @Query already ranks by relevance (_score)
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getPageSize());

        Page<SkillIndexDocument> resultPage;
        String keyword = request.getKeyword().trim();

        if (StringUtils.hasText(request.getSkillType())) {
            resultPage = indexRepository.searchByKeywordAndType(keyword, request.getSkillType(), pageable);
        } else {
            resultPage = indexRepository.searchByKeyword(keyword, pageable);
        }

        List<SkillSearchResponse> items = resultPage.getContent().stream()
                .map(this::toResponse)
                .toList();

        return PageResponse.of(resultPage.getTotalElements(), request.getPage(), request.getPageSize(), items);
    }

    // ---- internal helpers ----

    private void validateRequest(SkillSearchRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.VALIDATION001);
        }
        if (!StringUtils.hasText(request.getKeyword())) {
            throw new BusinessException("SEARCH001", "Search keyword must not be blank", 400);
        }
        if (request.getPage() < 1) {
            throw new BusinessException("SEARCH002", "Page number must be >= 1", 400);
        }
        if (request.getPageSize() < 1) {
            throw new BusinessException("SEARCH003", "Page size must be >= 1", 400);
        }
    }

    private SkillSearchResponse toResponse(SkillIndexDocument doc) {
        return SkillSearchResponse.builder()
                .id(doc.getId())
                .name(doc.getName())
                .description(doc.getDescription())
                .skillType(doc.getSkillType())
                .category(doc.getCategory())
                .tags(doc.getTags())
                .ownerName(doc.getOwnerName())
                .downloadCount(doc.getDownloadCount())
                .avgRating(doc.getAvgRating())
                .build();
    }
}
