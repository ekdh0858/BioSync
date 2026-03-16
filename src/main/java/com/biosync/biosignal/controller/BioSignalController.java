package com.biosync.biosignal.controller;

import com.biosync.biosignal.dto.BioSignalBatchUploadRequest;
import com.biosync.biosignal.service.BioSignalService;
import com.biosync.common.api.ApiResponse;
import com.biosync.security.AuthenticatedUser;
import jakarta.validation.Valid;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/biosignals")
@RequiredArgsConstructor
public class BioSignalController {

    private final BioSignalService bioSignalService;

    @PostMapping("/batch")
    public ResponseEntity<ApiResponse<?>> upload(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @Valid @RequestBody BioSignalBatchUploadRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(bioSignalService.upload(authenticatedUser.getId(), request)));
    }

    @GetMapping
    public ApiResponse<?> getSignals(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestParam Instant from,
            @RequestParam Instant to,
            @RequestParam(required = false) Long deviceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size
    ) {
        return ApiResponse.success(bioSignalService.getSignals(authenticatedUser.getId(), from, to, deviceId, page, size));
    }
}
