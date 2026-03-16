package com.biosync.biosignal.service;

import com.biosync.alert.domain.Alert;
import com.biosync.alert.domain.AlertSeverity;
import com.biosync.alert.domain.AlertType;
import com.biosync.alert.repository.AlertRepository;
import com.biosync.biosignal.domain.BioSignal;
import com.biosync.biosignal.dto.BioSignalBatchUploadRequest;
import com.biosync.biosignal.dto.BioSignalBatchUploadResponse;
import com.biosync.biosignal.dto.BioSignalItemRequest;
import com.biosync.biosignal.dto.BioSignalResponse;
import com.biosync.biosignal.repository.BioSignalRepository;
import com.biosync.common.api.PageResponse;
import com.biosync.common.exception.ApiException;
import com.biosync.device.domain.Device;
import com.biosync.device.service.DeviceService;
import com.biosync.user.domain.User;
import com.biosync.user.repository.UserRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BioSignalService {

    private static final ZoneId KOREA = ZoneId.of("Asia/Seoul");

    private final BioSignalRepository bioSignalRepository;
    private final AlertRepository alertRepository;
    private final DeviceService deviceService;
    private final UserRepository userRepository;

    public BioSignalBatchUploadResponse upload(Long userId, BioSignalBatchUploadRequest request) {
        if (request.signals().size() > 1000) {
            throw new ApiException("INVALID_INPUT", "signals size must be less than or equal to 1000", HttpStatus.BAD_REQUEST);
        }

        Device device = deviceService.getOwnedDevice(request.deviceId(), userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("NOT_FOUND", "User not found", HttpStatus.NOT_FOUND));
        Instant now = Instant.now();
        List<BioSignal> savedSignals = new ArrayList<>();
        int failedCount = 0;

        for (BioSignalItemRequest item : request.signals()) {
            if (!item.hasMetric() || item.recordedAt().isAfter(now)) {
                failedCount++;
                continue;
            }

            BioSignal bioSignal = bioSignalRepository.save(BioSignal.builder()
                    .user(user)
                    .device(device)
                    .recordedAt(item.recordedAt())
                    .recordedDateKr(item.recordedAt().atZone(KOREA).toLocalDate())
                    .heartRate(item.heartRate())
                    .stepCount(item.stepCount())
                    .sleepMinutes(item.sleepMinutes())
                    .stressLevel(item.stressLevel())
                    .uploadRequestId(request.uploadRequestId())
                    .build());
            savedSignals.add(bioSignal);
            createHighHeartRateAlertIfNeeded(user, device, bioSignal);
        }

        return new BioSignalBatchUploadResponse(
                device.getId(),
                request.uploadRequestId(),
                request.signals().size(),
                savedSignals.size(),
                failedCount,
                Instant.now());
    }

    @Transactional(readOnly = true)
    public PageResponse<BioSignalResponse> getSignals(
            Long userId,
            Instant from,
            Instant to,
            Long deviceId,
            int page,
            int size
    ) {
        var pageable = PageRequest.of(page, size);
        var signalPage = deviceId == null
                ? bioSignalRepository.findByUserIdAndRecordedAtBetween(userId, from, to, pageable)
                : bioSignalRepository.findByUserIdAndDeviceIdAndRecordedAtBetween(userId, deviceId, from, to, pageable);

        return PageResponse.from(signalPage.map(this::toResponse));
    }

    @Transactional(readOnly = true)
    public List<BioSignal> findByUserAndDate(Long userId, LocalDate date) {
        return bioSignalRepository.findByUserIdAndRecordedDateKr(userId, date);
    }

    @Transactional(readOnly = true)
    public Instant latestUploadAt(Long userId) {
        return bioSignalRepository.findTopByUserIdOrderByRecordedAtDesc(userId)
                .map(BioSignal::getRecordedAt)
                .orElse(null);
    }

    private void createHighHeartRateAlertIfNeeded(User user, Device device, BioSignal bioSignal) {
        if (bioSignal.getHeartRate() == null || bioSignal.getHeartRate() <= 120) {
            return;
        }

        var existing = alertRepository.findTopByUserIdAndTypeAndResolvedFalseOrderByOccurredAtDesc(
                user.getId(),
                AlertType.HIGH_HEART_RATE);

        if (existing.isPresent() && existing.get().getOccurredAt().isAfter(bioSignal.getRecordedAt().minusSeconds(600))) {
            return;
        }

        alertRepository.save(Alert.builder()
                .user(user)
                .device(device)
                .bioSignal(bioSignal)
                .type(AlertType.HIGH_HEART_RATE)
                .severity(AlertSeverity.CRITICAL)
                .message("Heart rate exceeded threshold")
                .occurredAt(bioSignal.getRecordedAt())
                .resolved(false)
                .build());
    }

    private BioSignalResponse toResponse(BioSignal bioSignal) {
        return new BioSignalResponse(
                bioSignal.getId(),
                bioSignal.getDevice().getId(),
                bioSignal.getRecordedAt(),
                bioSignal.getHeartRate(),
                bioSignal.getStepCount(),
                bioSignal.getSleepMinutes(),
                bioSignal.getStressLevel());
    }
}
