package com.skill.platform;

import com.skill.platform.auth.model.Role;
import com.skill.platform.auth.model.User;
import com.skill.platform.core.model.Skill;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class TestDataFactory {

    public static User createTestUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmployeeId("EMP" + System.currentTimeMillis());
        user.setName("Test User");
        user.setEmail("test" + System.currentTimeMillis() + "@example.com");
        user.setPasswordHash("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"); // password
        user.setDepartment("Engineering");
        user.setStatus("active");
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        return user;
    }

    public static Skill createTestSkill(User owner) {
        Skill skill = new Skill();
        skill.setId(UUID.randomUUID());
        skill.setName("Test Skill " + System.currentTimeMillis());
        skill.setDescription("A test skill for unit testing");
        skill.setSkillType("public");
        skill.setCategory("automation");
        skill.setStatus("draft");
        skill.setOwner(owner);
        skill.setVersion("1.0.0");
        skill.setDownloadCount(0);
        skill.setAvgRating(BigDecimal.ZERO);
        skill.setRatingCount(0);
        skill.setCreatedAt(Instant.now());
        skill.setUpdatedAt(Instant.now());
        return skill;
    }

    public static Role createTestRole(String name) {
        Role role = new Role();
        role.setId(UUID.randomUUID());
        role.setName(name);
        role.setDisplayName(name + " Role");
        role.setIsSystem(true);
        role.setCreatedAt(Instant.now());
        role.setUpdatedAt(Instant.now());
        return role;
    }
}
