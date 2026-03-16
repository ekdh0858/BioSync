package com.biosync.alert.service;

import com.biosync.alert.domain.Alert;
import com.biosync.alert.domain.AlertSeverity;
import com.biosync.alert.domain.AlertType;
import com.biosync.alert.dto.AlertResponse;
import com.biosync.alert.repository.AlertRepository;
import com.biosync.biosignal.repository.BioSignalRepository;
import com.biosync.common.api.PageResponse;
import com.biosync.common.exception.ApiException;
import com.biosync.device.domain.Device;
import com.biosync.device.domain.DeviceStatus;
import com.biosync.device.repository.DeviceRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AlertService {

    private static final ZoneId KOREA = ZoneId.of("Asia/Seoul");

    private final AlertRepository alertRepository;
    private final DeviceRepository deviceRepository;
    private final BioSignalRepository bioSignalRepository;

    @Transactional(readOnly = true)
    public PageResponse<AlertResponse> getUserAlerts(Long userId, AlertSeverity severity, LocalDate date, int page, int size) {
        Page<Alert> alertPage = loadUserAlerts(userId, severity, date, page, size);
        return PageResponse.from(alertPage.map(this::toResponse));
    }

    @Transactional(readOnly = true)
    public PageResponse<AlertResponse> getAdminAlerts(AlertSeverity severity, Boolean resolved, int page, int size) {
        var pageable = PageRequest.of(page, size);
        Page<Alert> alertPage;

        if (severity != null && resolved != null) {
            alertPage = alertRepository.findBySeverityAndResolved(severity, resolved, pageable);
        } else if (severity != null) {
            alertPage = alertRepository.findBySeverity(severity, pageable);
        } else if (resolved != null) {
            alertPage = alertRepository.findByResolved(resolved, pageable);
        } else {
            alertPage = alertRepository.findAll(pageable);
        }

        return PageResponse.from(alertPage.map(this::toResponse));
    }

    @Transactional(readOnly = true)
    public java.util.List<AlertResponse> recentAlertsForUser(Long userId) {
        return alertRepository.findTop10ByUserIdOrderByOccurredAtDesc(userId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public long countCriticalAlerts(LocalDate date) {
        Instant start = date.atStartOfDay(KOREA).toInstant();
        Instant end = date.plusDays(1).atStartOfDay(KOREA).toInstant();
        return alertRepository.countBySeverityAndOccurredAtBetween(AlertSeverity.CRITICAL, start, end);
    }

    public void createUploadMissingAlerts(LocalDate date) {
        Map<Long, Device> activeDeviceByUser = deviceRepository.findByStatus(DeviceStatus.ACTIVE).stream()
                .collect(java.util.stream.Collectors.toMap(device -> device.getUser().getId(), device -> device, (left, right) -> left));

        activeDeviceByUser.forEach((userId, device) -> {
            if (bioSignalRepository.countByUserIdAndRecordedDateKr(userId, date) > 0) {
                return;
            }

            var existing = alertRepository.findTopByUserIdAndTypeAndResolvedFalseOrderByOccurredAtDesc(userId, AlertType.UPLOAD_MISSING);
            if (existing.isPresent() && existing.get().getOccurredAt().atZone(KOREA).toLocalDate().equals(date)) {
                return;
            }

            alertRepository.save(Alert.builder()
                    .user(device.getUser())
                    .device(device)
                    .type(AlertType.UPLOAD_MISSING)
                    .severity(AlertSeverity.WARNING)
                    .message("No biosignal uploaded for the day")
                    .occurredAt(date.atTime(23, 30).atZone(KOREA).toInstant())
                    .resolved(false)
                    .build());
        });
    }

    public Alert resolve(Long alertId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new ApiException("NOT_FOUND", "Alert not found", HttpStatus.NOT_FOUND));
        alert.resolve(Instant.now());
        return alert;
    }

    public AlertResponse toResponse(Alert alert) {
        return new AlertResponse(
                alert.getId(),
                alert.getType(),
                alert.getSeverity(),
                alert.getMessage(),
                alert.getOccurredAt(),
                alert.isResolved());
    }

    private Page<Alert> loadUserAlerts(Long userId, AlertSeverity severity, LocalDate date, int page, int size) {
        var pageable = PageRequest.of(page, size);

        if (date != null) {
            Instant start = date.atStartOfDay(KOREA).toInstant();
            Instant end = date.plusDays(1).atStartOfDay(KOREA).toInstant();
            return severity == null
                    ? alertRepository.findByUserIdAndOccurredAtBetween(userId, start, end, pageable)
                    : alertRepository.findByUserIdAndSeverityAndOccurredAtBetween(userId, severity, start, end, pageable);
        }

        return severity == null
                ? alertRepository.findByUserId(userId, pageable)
                : alertRepository.findByUserIdAndSeverity(userId, severity, pageable);
    }
}
