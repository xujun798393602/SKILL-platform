package com.skill.platform.social.controller;

import com.skill.platform.common.response.ApiResponse;
import com.skill.platform.common.response.PageResponse;
import com.skill.platform.social.model.dto.RatingRequest;
import com.skill.platform.social.model.dto.RatingResponse;
import com.skill.platform.social.service.RatingService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for SKILL rating endpoints (F013 - SKILL评价服务).
 * <p>
 * Provides endpoints for submitting ratings on downloaded SKILLs,
 * listing ratings for a specific SKILL (paginated), and listing
 * the current user's own ratings.
 */
@RestController
@RequestMapping("/api/v1")
@Slf4j
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    /**
     * Submit a rating for a downloaded SKILL.
     * <p>
     * Validates that the user has downloaded the SKILL and has not already rated it.
     * Returns the created rating on success.
     *
     * @param skillId the ID of the SKILL to rate
     * @param request the rating request (score 1-5, optional comment max 500 chars)
     * @return the created rating
     */
    @PostMapping("/skills/{skillId}/ratings")
    public ApiResponse<RatingResponse> submitRating(
            @PathVariable UUID skillId,
            @Valid @RequestBody RatingRequest request) {

        log.info("POST /api/v1/skills/{}/ratings - rating={}, commentLength={}",
                skillId, request.getRating(),
                request.getComment() != null ? request.getComment().length() : 0);

        RatingResponse response = ratingService.submitRating(skillId, request);
        return ApiResponse.success("Rating submitted successfully", response);
    }

    /**
     * List all ratings for a specific SKILL, ordered by creation time descending.
     *
     * @param skillId  the SKILL ID
     * @param page     1-based page number (defaults to 1)
     * @param pageSize number of items per page (defaults to 20)
     * @return paginated list of ratings for the SKILL
     */
    @GetMapping("/skills/{skillId}/ratings")
    public ApiResponse<PageResponse<RatingResponse>> listRatings(
            @PathVariable UUID skillId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {

        log.info("GET /api/v1/skills/{}/ratings - page={}, pageSize={}", skillId, page, pageSize);

        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<RatingResponse> ratingPage = ratingService.listRatingsBySkill(skillId, pageable);
        return ApiResponse.success(PageResponse.of(ratingPage));
    }

    /**
     * List all ratings submitted by the current authenticated user.
     *
     * @param page     1-based page number (defaults to 1)
     * @param pageSize number of items per page (defaults to 20)
     * @return paginated list of the current user's ratings
     */
    @GetMapping("/ratings/my")
    public ApiResponse<PageResponse<RatingResponse>> listMyRatings(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {

        log.info("GET /api/v1/ratings/my - page={}, pageSize={}", page, pageSize);

        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<RatingResponse> ratingPage = ratingService.listMyRatings(pageable);
        return ApiResponse.success(PageResponse.of(ratingPage));
    }
}
