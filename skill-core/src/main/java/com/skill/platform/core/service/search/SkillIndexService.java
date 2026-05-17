package com.skill.platform.core.service.search;

import com.skill.platform.core.model.SkillIndexDocument;
import com.skill.platform.core.repository.SkillIndexRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class SkillIndexService {

    private final SkillIndexRepository indexRepository;

    public SkillIndexService(SkillIndexRepository indexRepository) {
        this.indexRepository = indexRepository;
    }

    public void indexSkill(Skill skill, String ownerName, List<String> tags) {
        SkillIndexDocument document = SkillIndexDocument.builder()
            .id(skill.getId().toString())
            .name(skill.getName())
            .description(skill.getDescription())
            .skillType(skill.getSkillType())
            .category(skill.getCategory())
            .tags(tags)
            .ownerName(ownerName)
            .downloadCount(skill.getDownloadCount())
            .avgRating(skill.getAvgRating() != null ? skill.getAvgRating().doubleValue() : 0.0)
            .status(skill.getStatus())
            .createdAt(skill.getCreatedAt())
            .suggest(Arrays.asList(skill.getName(), skill.getCategory()))
            .build();

        indexRepository.save(document);
        log.info("Indexed skill: {}", skill.getId());
    }

    public void removeSkill(String skillId) {
        indexRepository.deleteById(skillId);
        log.info("Removed skill from index: {}", skillId);
    }

    public void updateSkillStatus(String skillId, String status) {
        indexRepository.findById(skillId).ifPresent(doc -> {
            doc.setStatus(status);
            indexRepository.save(doc);
        });
    }
}
