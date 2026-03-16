package com.biosync.admin.service;

import com.biosync.admin.dto.AdminUserDetailResponse;
import com.biosync.admin.dto.AdminUserListItemResponse;
import com.biosync.admin.dto.IngestionStatusResponse;
import com.biosync.admin.dto.ResolveAlertResponse;
import com.biosync.alert.domain.AlertSeverity;
import com.biosync.alert.dto.AlertResponse;
import com.biosync.alert.service.AlertService;
import com.biosync.biosignal.repository.BioSignalRepository;
import com.biosync.biosignal.service.BioSignalService;
import com.biosync.common.api.PageResponse;
import com.biosync.common.exception.ApiException;
import com.biosync.device.service.DeviceService;
import com.biosync.summary.dto.DailySummaryResponse;
import com.biosync.summary.service.SummaryService;
import com.biosync.user.domain.User;
import com.biosync.user.repository.UserRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final UserRepository userRepository;
    private final DeviceService deviceService;
    private final BioSignalService bioSignalService;
    private final SummaryService summaryService;
    private final AlertService alertService;
    private final BioSignalRepository bioSignalRepository;

    @Transactional(readOnly = true)
    public PageResponse<AdminUserListItemResponse> getUsers(String keyword, int page, int size) {
        var pageable = PageRequest.of(page, size);
        var userPage = (keyword == null || keyword.isBlank())
                ? userRepository.findAll(pageable)
                : userRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword, pageable);

        return PageResponse.from(userPage.map(this::toListItem));
    }

    @Transactional(readOnly = true)
    public AdminUserDetailResponse getUserDetail(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("NOT_FOUND", "User not found", HttpStatus.NOT_FOUND));

        return new AdminUserDetailResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getBirthDate(),
                user.getRole(),
                user.getStatus(),
                user.getLastLoginAt(),
                bioSignalService.latestUploadAt(userId),
                deviceService.getDevices(userId),
                alertService.recentAlertsForUser(userId));
    }

    @Transactional(readOnly = true)
    public DailySummaryResponse getUserSummary(Long userId, LocalDate date) {
        return summaryService.getDailySummary(userId, date);
    }

    @Transactional(readOnly = true)
    public PageResponse<AlertResponse> getAlerts(AlertSeverity severity, Boolean resolved, int page, int size) {
        return alertService.getAdminAlerts(severity, resolved, page, size);
    }

    @Transactional(readOnly = true)
    public IngestionStatusResponse getIngestionStatus(LocalDate date) {
        List<User> users = userRepository.findAll();
        long totalRegisteredUsers = users.size();
        long successfulUploadUsers = users.stream()
                .filter(user -> bioSignalRepository.countByUserIdAndRecordedDateKr(user.getId(), date) > 0)
                .count();
        long missingUsers = totalRegisteredUsers - successfulUploadUsers;
        long uploadFailureCount = missingUsers;
        long criticalAlertCount = alertService.countCriticalAlerts(date);

        return new IngestionStatusResponse(
                date,
                totalRegisteredUsers,
                successfulUploadUsers,
                uploadFailureCount,
                missingUsers,
                criticalAlertCount);
    }

    public ResolveAlertResponse resolveAlert(Long alertId) {
        var alert = alertService.resolve(alertId);
        return new ResolveAlertResponse(alert.getId(), alert.isResolved(), alert.getResolvedAt());
    }

    private AdminUserListItemResponse toListItem(User user) {
        return new AdminUserListItemResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole(),
                user.getStatus(),
                user.getLastLoginAt());
    }
}
