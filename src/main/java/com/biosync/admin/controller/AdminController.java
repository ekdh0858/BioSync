package com.biosync.admin.controller;

import com.biosync.admin.dto.ResolveAlertRequest;
import com.biosync.admin.service.AdminService;
import com.biosync.alert.domain.AlertSeverity;
import com.biosync.common.api.ApiResponse;
import jakarta.validation.Valid;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public ApiResponse<?> getUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.success(adminService.getUsers(keyword, page, size));
    }

    @GetMapping("/users/{userId}")
    public ApiResponse<?> getUserDetail(@PathVariable Long userId) {
        return ApiResponse.success(adminService.getUserDetail(userId));
    }

    @GetMapping("/users/{userId}/summaries/daily")
    public ApiResponse<?> getUserSummary(@PathVariable Long userId, @RequestParam LocalDate date) {
        return ApiResponse.success(adminService.getUserSummary(userId, date));
    }

    @GetMapping("/alerts")
    public ApiResponse<?> getAlerts(
            @RequestParam(required = false) AlertSeverity severity,
            @RequestParam(required = false) Boolean resolved,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return ApiResponse.success(adminService.getAlerts(severity, resolved, page, size));
    }

    @GetMapping("/monitoring/ingestion-status")
    public ApiResponse<?> getIngestionStatus(@RequestParam LocalDate date) {
        return ApiResponse.success(adminService.getIngestionStatus(date));
    }

    @PatchMapping("/alerts/{alertId}/resolve")
    public ResponseEntity<ApiResponse<?>> resolveAlert(
            @PathVariable Long alertId,
            @Valid @RequestBody ResolveAlertRequest request
    ) {
        if (!request.resolved()) {
            return ResponseEntity.badRequest().body(ApiResponse.failure("INVALID_INPUT", "resolved must be true"));
        }

        return ResponseEntity.ok(ApiResponse.success(adminService.resolveAlert(alertId)));
    }
}
