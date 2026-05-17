package com.skill.platform.social.controller;

import com.skill.platform.common.response.ApiResponse;
import com.skill.platform.common.response.PageResponse;
import com.skill.platform.social.model.dto.ShareResponse;
import com.skill.platform.social.service.ShareService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST controller for SKILL share operations (F015).
 * <p>
 * Exposes endpoints for generating share links, accessing shared skills,
 * and listing the current user's share links.
 */
@RestController
@RequestMapping("/api/v1")
@Slf4j
public class ShareController {

    private final ShareService shareService;

    public ShareController(ShareService shareService) {
        this.shareService = shareService;
    }

    /**
     * Generate a share link for the specified skill.
     * <p>
     * The skill must be in "published" status. Returns a unique share token,
     * the full share URL, and the expiration timestamp.
     *
     * @param skillId the skill to share
     * @return the share link details
     */
    @PostMapping("/skills/{skillId}/share")
    public ApiResponse<ShareResponse> createShare(@PathVariable UUID skillId) {
        log.info("Share request received: skillId={}", skillId);
        ShareResponse response = shareService.createShare(skillId);
        return ApiResponse.success("Share link generated", response);
    }

    /**
     * Access a shared skill by its token (public endpoint, no auth required).
     * <p>
     * Validates the token exists and has not expired, increments the access count,
     * and returns the skill details.
     *
     * @param token the share token
     * @return the shared skill details
     */
    @GetMapping("/shared/{token}")
    public ApiResponse<ShareResponse> accessShare(@PathVariable String token) {
        log.info("Share access request: token={}...", token.substring(0, Math.min(8, token.length())));
        ShareResponse response = shareService.accessShare(token);
        return ApiResponse.success(response);
    }

    /**
     * List the current user's share links with pagination.
     *
     * @param pageable pagination parameters
     * @return a paginated list of the user's share links
     */
    @GetMapping("/shares/my")
    public ApiResponse<PageResponse<ShareResponse>> listMyShares(Pageable pageable) {
        return ApiResponse.success(shareService.listMyShares(pageable));
    }
}
