package com.skill.platform.core.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.InputStream;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillDownloadResponse {
    private InputStream inputStream;
    private String fileName;
    private String contentType;
}
