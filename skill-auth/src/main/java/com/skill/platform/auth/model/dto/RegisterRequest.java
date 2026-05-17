package com.skill.platform.auth.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Employee ID is required")
    @Size(min = 6, max = 20)
    private String employeeId;

    @NotBlank(message = "Name is required")
    @Size(max = 100)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 200)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 50)
    private String password;

    @NotBlank(message = "Department is required")
    @Size(max = 100)
    private String department;
}
