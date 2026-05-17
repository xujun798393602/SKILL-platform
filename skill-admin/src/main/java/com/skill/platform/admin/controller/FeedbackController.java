package com.skill.platform.admin.controller;

import com.skill.platform.admin.model.dto.FeedbackRequest;
import com.skill.platform.admin.model.dto.FeedbackResponse;
import com.skill.platform.admin.service.FeedbackService;
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

import java.util.Map;
import java.util.UUID;

/**
 * REST controller for feedback management.
 * <p>
 * User endpoints are under {@code /api/v1/feedbacks}.
 * Admin endpoints are under {@code /api/v1/admin/feedbacks}.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    // ---- User endpoints ----

    /**
     * Submit new feedback.
     *
     * @param request the feedback submission request
     * @return API response containing the created feedback
     */
    @PostMapping("/api/v1/feedbacks")
    public ApiResponse<FeedbackResponse> submitFeedback(@Valid @RequestBody FeedbackRequest request) {
        FeedbackResponse response = feedbackService.submitFeedback(request);
        return ApiResponse.success("Feedback submitted successfully", response);
    }

    /**
     * List the current user's feedbacks with pagination.
     *
     * @param page     page number (default 1)
     * @param pageSize page size (default 20)
     * @return API response containing paginated feedbacks
     */
    @GetMapping("/api/v1/feedbacks")
    public ApiResponse<PageResponse<FeedbackResponse>> listMyFeedbacks(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {

        PageRequest pageRequest = PageRequest.builder()
                .page(page)
                .pageSize(pageSize)
                .sortBy("createdAt")
                .sortOrder("desc")
                .build();

        PageResponse<FeedbackResponse> result = feedbackService.listMyFeedbacks(pageRequest);
        return ApiResponse.success(result);
    }

    /**
     * Get feedback detail by ID.
     *
     * @param id the feedback ID
     * @return API response containing the feedback detail
     */
    @GetMapping("/api/v1/feedbacks/{id}")
    public ApiResponse<FeedbackResponse> getFeedback(@PathVariable("id") UUID id) {
        FeedbackResponse response = feedbackService.getFeedback(id);
        return ApiResponse.success(response);
    }

    // ---- Admin endpoints ----

    /**
     * List all feedbacks (admin only) with optional status filter and pagination.
     *
     * @param status   optional status filter (e.g. "pending", "replied")
     * @param page     page number (default 1)
     * @param pageSize page size (default 20)
     * @return API response containing paginated feedbacks
     */
    @GetMapping("/api/v1/admin/feedbacks")
    public ApiResponse<PageResponse<FeedbackResponse>> listAllFeedbacks(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {

        requireAdmin();

        PageRequest pageRequest = PageRequest.builder()
                .page(page)
                .pageSize(pageSize)
                .sortBy("createdAt")
                .sortOrder("desc")
                .build();

        PageResponse<FeedbackResponse> result = feedbackService.listAllFeedbacks(status, pageRequest);
        return ApiResponse.success(result);
    }

    /**
     * Admin reply to a feedback item.
     *
     * @param id      the feedback ID to reply to
     * @param request the reply request body containing the reply text
     * @return API response containing the updated feedback
     */
    @PostMapping("/api/v1/admin/feedbacks/{id}/reply")
    public ApiResponse<FeedbackResponse> replyToFeedback(
            @PathVariable("id") UUID id,
            @RequestBody Map<String, String> request) {

        requireAdmin();

        String reply = request.get("reply");
        if (reply == null || reply.isBlank()) {
            throw new BusinessException(
                    ErrorCode.VALIDATION001.getCode(),
                    "Reply text must not be blank",
                    ErrorCode.VALIDATION001.getHttpStatus()
            );
        }

        FeedbackResponse response = feedbackService.replyToFeedback(id, reply);
        return ApiResponse.success("Reply sent successfully", response);
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
