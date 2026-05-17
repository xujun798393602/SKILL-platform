package com.skill.platform.review.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

/**
 * Application event published when a skill review is completed.
 * Used to notify the skill uploader of the review outcome.
 */
@Getter
@Builder
@AllArgsConstructor
public class SkillReviewedEvent {

    private final UUID skillId;
    private final UUID reviewerId;
    private final String action;
    private final String comment;
}
