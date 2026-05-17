package com.skill.platform.social.controller;

import com.skill.platform.common.response.ApiResponse;
import com.skill.platform.common.response.PageResponse;
import com.skill.platform.social.model.dto.FavoriteResponse;
import com.skill.platform.social.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for SKILL favorite operations (F014).
 * <p>
 * Endpoints:
 * <ul>
 *   <li>{@code POST   /api/v1/favorites/{skillId}} - add a favorite</li>
 *   <li>{@code DELETE  /api/v1/favorites/{skillId}} - remove a favorite</li>
 *   <li>{@code GET    /api/v1/favorites}            - list favorites (paginated)</li>
 * </ul>
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    /**
     * Add a skill to the current user's favorites.
     *
     * @param skillId the skill to favorite
     * @return success response with 201 status
     */
    @PostMapping("/{skillId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Void> addFavorite(@PathVariable UUID skillId) {
        log.info("POST /api/v1/favorites/{}", skillId);
        favoriteService.addFavorite(skillId);
        return ApiResponse.success("Skill added to favorites");
    }

    /**
     * Remove a skill from the current user's favorites.
     *
     * @param skillId the skill to unfavorite
     * @return success response
     */
    @DeleteMapping("/{skillId}")
    public ApiResponse<Void> removeFavorite(@PathVariable UUID skillId) {
        log.info("DELETE /api/v1/favorites/{}", skillId);
        favoriteService.removeFavorite(skillId);
        return ApiResponse.success("Skill removed from favorites");
    }

    /**
     * List the current user's favorites with pagination.
     *
     * @param pageable pagination parameters (defaults: page=0, size=20)
     * @return paginated list of favorites
     */
    @GetMapping
    public ApiResponse<PageResponse<FavoriteResponse>> listFavorites(
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("GET /api/v1/favorites, page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        PageResponse<FavoriteResponse> favorites = favoriteService.listFavorites(pageable);
        return ApiResponse.success(favorites);
    }
}
