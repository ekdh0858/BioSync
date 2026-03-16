package com.biosync.auth.dto;

import com.biosync.user.domain.Role;

public record SignUpResponse(Long userId, String email, String name, Role role) {
}
