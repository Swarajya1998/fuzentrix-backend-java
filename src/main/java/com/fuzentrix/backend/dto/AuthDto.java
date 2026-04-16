package com.fuzentrix.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AuthDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        // @Pattern covers format validation; @Email is redundant and removed.
        @NotBlank(message = "Email is required")
        @Pattern(
                regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
                message = "Email must be a valid and properly formatted address"
        )
        private String email;

        @NotBlank(message = "Password is required")
        private String password;

        private String deviceInfo;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterRequest {
        // @Pattern covers format validation; @Email is redundant and removed.
        @NotBlank(message = "Email is required")
        @Pattern(
                regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
                message = "Email must be a valid and properly formatted address"
        )
        private String email;

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters long")
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!_\\-]).*$",
                message = "Password must contain at least one digit, one lowercase, one uppercase, and one special character (@#$%^&+=!_-)"
        )
        private String password;
    }
}
