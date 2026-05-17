package com.skill.platform.auth.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Employee ID is required")
    @Size(min = 6, max = 20, message = "Employee ID must be 6-20 characters")
    private String employeeId;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 50, message = "Password must be 8-50 characters")
    private String password;

    private String loginType = "password";
}
