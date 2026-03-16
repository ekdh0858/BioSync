package com.biosync.user.controller;

import com.biosync.common.api.ApiResponse;
import com.biosync.security.AuthenticatedUser;
import com.biosync.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ApiResponse<?> getMe(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return ApiResponse.success(userService.getMe(authenticatedUser.getId()));
    }
}
