package com.biosync.user.dto;

import com.biosync.user.domain.Role;
import java.time.Instant;
import java.time.LocalDate;

public record UserMeResponse(
        Long userId,
        String email,
        String name,
        LocalDate birthDate,
        Role role,
        Instant createdAt
) {
}
