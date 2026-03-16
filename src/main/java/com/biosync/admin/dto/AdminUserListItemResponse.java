package com.biosync.admin.dto;

import com.biosync.user.domain.Role;
import com.biosync.user.domain.UserStatus;
import java.time.Instant;

public record AdminUserListItemResponse(
        Long userId,
        String email,
        String name,
        Role role,
        UserStatus status,
        Instant lastLoginAt
) {
}
