package com.skill.platform.admin.service;

import com.skill.platform.admin.model.Feedback;
import com.skill.platform.admin.model.dto.FeedbackRequest;
import com.skill.platform.admin.model.dto.FeedbackResponse;
import com.skill.platform.admin.repository.FeedbackRepository;
import com.skill.platform.auth.model.User;
import com.skill.platform.auth.repository.UserRepository;
import com.skill.platform.common.exception.BusinessException;
import com.skill.platform.common.exception.ErrorCode;
import com.skill.platform.common.response.PageResponse;
import com.skill.platform.common.util.PageRequest;
import com.skill.platform.common.util.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Service for managing user feedback.
 * <p>
 * Supports feedback submission by regular users and admin reply functionality.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    /**
     * Submit new feedback on behalf of the current user.
     *
     * @param request the feedback submission request
     * @return the created feedback response
     * @throws BusinessException if the current user is not found
     */
    @Transactional
    public FeedbackResponse submitFeedback(FeedbackRequest request) {
        User currentUser = getCurrentUser();

        Feedback feedback = Feedback.builder()
                .user(currentUser)
                .title(request.getTitle())
                .content(request.getContent())
                .category(request.getCategory())
                .status("pending")
                .build();

        Feedback saved = feedbackRepository.save(feedback);
        log.info("Feedback submitted: id={}, userId={}", saved.getId(), currentUser.getId());
        return FeedbackResponse.fromEntity(saved);
    }

    /**
     * List feedbacks submitted by the current user, with pagination.
     *
     * @param pageRequest pagination parameters
     * @return paginated feedback responses
     */
    @Transactional(readOnly = true)
    public PageResponse<FeedbackResponse> listMyFeedbacks(PageRequest pageRequest) {
        UUID userId = UUID.fromString(UserContext.getUserId());

        var page = feedbackRepository.findByUserId(userId, pageRequest.toPageable());
        var responses = page.getContent().stream()
                .map(FeedbackResponse::fromEntity)
                .toList();

        return PageResponse.of(page.getTotalElements(), page.getNumber() + 1,
                page.getSize(), responses);
    }

    /**
     * Get a single feedback detail by ID.
     *
     * @param feedbackId the feedback ID
     * @return the feedback response
     * @throws BusinessException if the feedback is not found
     */
    @Transactional(readOnly = true)
    public FeedbackResponse getFeedback(UUID feedbackId) {
        Feedback feedback = findFeedbackById(feedbackId);
        return FeedbackResponse.fromEntity(feedback);
    }

    /**
     * List all feedbacks with optional status filter (admin only).
     *
     * @param status     optional status filter
     * @param pageRequest pagination parameters
     * @return paginated feedback responses
     */
    @Transactional(readOnly = true)
    public PageResponse<FeedbackResponse> listAllFeedbacks(String status, PageRequest pageRequest) {
        var page = (status != null && !status.isBlank())
                ? feedbackRepository.findByStatus(status, pageRequest.toPageable())
                : feedbackRepository.findAll(pageRequest.toPageable());

        var responses = page.getContent().stream()
                .map(FeedbackResponse::fromEntity)
                .toList();

        return PageResponse.of(page.getTotalElements(), page.getNumber() + 1,
                page.getSize(), responses);
    }

    /**
     * Admin reply to a feedback item.
     *
     * @param feedbackId the feedback ID to reply to
     * @param replyText  the reply content
     * @return the updated feedback response
     * @throws BusinessException if the feedback is not found
     */
    @Transactional
    public FeedbackResponse replyToFeedback(UUID feedbackId, String replyText) {
        Feedback feedback = findFeedbackById(feedbackId);
        User adminUser = getCurrentUser();

        feedback.setReply(replyText);
        feedback.setRepliedBy(adminUser);
        feedback.setRepliedAt(Instant.now());
        feedback.setStatus("replied");

        Feedback saved = feedbackRepository.save(feedback);
        log.info("Feedback replied: id={}, adminUserId={}", feedbackId, adminUser.getId());
        return FeedbackResponse.fromEntity(saved);
    }

    /**
     * Find a feedback by ID or throw if not found.
     *
     * @param feedbackId the feedback ID
     * @return the Feedback entity
     * @throws BusinessException if not found
     */
    private Feedback findFeedbackById(UUID feedbackId) {
        return feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new BusinessException(
                        "FEEDBACK001",
                        "Feedback not found",
                        404
                ));
    }

    /**
     * Get the current authenticated user from UserContext.
     *
     * @return the User entity
     * @throws BusinessException if the user is not found
     */
    private User getCurrentUser() {
        UUID userId = UUID.fromString(UserContext.getUserId());
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH001));
    }
}
