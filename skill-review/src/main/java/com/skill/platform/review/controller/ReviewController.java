package com.skill.platform.review.controller;

import com.skill.platform.common.exception.BusinessException;
import com.skill.platform.common.response.ApiResponse;
import com.skill.platform.common.response.PageResponse;
import com.skill.platform.common.util.UserContext;
import com.skill.platform.review.model.dto.ReviewRequest;
import com.skill.platform.review.model.dto.ReviewResponse;
import com.skill.platform.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for SKILL review endpoints.
 */
@RestController
@RequestMapping("/api/v1")
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /**
     * Submit a review decision for a skill.
     *
     * @param skillId the ID of the skill to review
     * @param request the review decision (action + comment)
     * @return the created review details
     */
    @PostMapping("/skills/{skillId}/review")
    public ApiResponse<ReviewResponse> submitReview(
            @PathVariable UUID skillId,
            @Valid @RequestBody ReviewRequest request) {

        log.info("POST /api/v1/skills/{}/review - action: {}", skillId, request.getAction());
        ReviewResponse response = reviewService.submitReview(skillId, request);
        return ApiResponse.success("Review submitted successfully", response);
    }

    /**
     * List reviews for a specific skill (paginated).
     *
     * @param skillId  the skill ID
     * @param page     1-based page number (defaults to 1)
     * @param pageSize number of items per page (defaults to 20)
     * @return paginated list of reviews
     */
    @GetMapping("/skills/{skillId}/reviews")
    public ApiResponse<PageResponse<ReviewResponse>> listReviewsBySkill(
            @PathVariable UUID skillId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {

        log.info("GET /api/v1/skills/{}/reviews - page={}, pageSize={}", skillId, page, pageSize);
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ReviewResponse> reviewPage = reviewService.listReviewsBySkill(skillId, pageable);
        return ApiResponse.success(PageResponse.of(reviewPage));
    }

    /**
     * List all reviews (admin only, paginated).
     *
     * @param page     1-based page number (defaults to 1)
     * @param pageSize number of items per page (defaults to 20)
     * @return paginated list of all reviews
     */
    @GetMapping("/reviews")
    public ApiResponse<PageResponse<ReviewResponse>> listAllReviews(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {

        log.info("GET /api/v1/reviews - page={}, pageSize={}", page, pageSize);

        // Only ADMIN can list all reviews
        if (!UserContext.hasRole("ADMIN")) {
            throw new BusinessException("AUTH003", "Only ADMIN can list all reviews", 403);
        }

        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ReviewResponse> reviewPage = reviewService.listAllReviews(pageable);
        return ApiResponse.success(PageResponse.of(reviewPage));
    }
}
