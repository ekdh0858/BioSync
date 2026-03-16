package com.biosync.device.controller;

import com.biosync.common.api.ApiResponse;
import com.biosync.device.dto.DeviceRegisterRequest;
import com.biosync.device.service.DeviceService;
import com.biosync.security.AuthenticatedUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @PostMapping
    public ResponseEntity<ApiResponse<?>> register(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @Valid @RequestBody DeviceRegisterRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(deviceService.register(authenticatedUser.getId(), request)));
    }

    @GetMapping
    public ApiResponse<?> getDevices(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return ApiResponse.success(deviceService.getDevices(authenticatedUser.getId()));
    }
}
