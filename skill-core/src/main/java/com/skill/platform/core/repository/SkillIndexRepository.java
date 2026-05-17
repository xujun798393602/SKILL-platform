package com.skill.platform.core.repository;

import com.skill.platform.core.model.SkillIndexDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface SkillIndexRepository extends ElasticsearchRepository<SkillIndexDocument, String> {

    List<SkillIndexDocument> findByStatus(String status);

    List<SkillIndexDocument> findBySkillTypeAndStatus(String skillType, String status);

    @Query("{\"bool\": {\"must\": [{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"name^3\", \"description\", \"tags^2\"]}}], \"filter\": [{\"term\": {\"status\": \"published\"}}]}}")
    Page<SkillIndexDocument> searchByKeyword(String keyword, Pageable pageable);

    @Query("{\"bool\": {\"must\": [{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"name^3\", \"description\", \"tags^2\"]}}], \"filter\": [{\"term\": {\"skillType\": \"?1\"}}, {\"term\": {\"status\": \"published\"}}]}}")
    Page<SkillIndexDocument> searchByKeywordAndType(String keyword, String skillType, Pageable pageable);
}
