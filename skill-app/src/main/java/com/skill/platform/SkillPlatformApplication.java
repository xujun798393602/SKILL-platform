package com.skill.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.skill.platform")
@EnableJpaRepositories(basePackages = "com.skill.platform.*.repository")
@EntityScan(basePackages = "com.skill.platform.*.model")
@EnableAsync
@EnableScheduling
public class SkillPlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(SkillPlatformApplication.class, args);
    }
}
