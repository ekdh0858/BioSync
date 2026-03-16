package com.biosync.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record SignUpRequest(
        @Email @NotBlank @Size(max = 255) String email,
        @NotBlank
        @Size(min = 8, max = 20)
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).+$",
                message = "password must include upper, lower, number and special character")
        String password,
        @NotBlank @Size(min = 2, max = 50) String name,
        LocalDate birthDate
) {
}
