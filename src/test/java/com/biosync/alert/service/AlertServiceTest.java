package com.biosync.alert.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.biosync.alert.domain.AlertType;
import com.biosync.alert.repository.AlertRepository;
import com.biosync.biosignal.repository.BioSignalRepository;
import com.biosync.device.domain.Device;
import com.biosync.device.domain.DeviceStatus;
import com.biosync.device.repository.DeviceRepository;
import com.biosync.user.domain.Role;
import com.biosync.user.domain.User;
import com.biosync.user.domain.UserStatus;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    @Mock
    private AlertRepository alertRepository;

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private BioSignalRepository bioSignalRepository;

    @InjectMocks
    private AlertService alertService;

    @Test
    void shouldCreateUploadMissingAlertForActiveDeviceOwnerWithoutSignals() {
        User user = User.builder()
                .email("user@example.com")
                .passwordHash("encoded")
                .name("Tester")
                .role(Role.USER)
                .status(UserStatus.ACTIVE)
                .build();
        ReflectionTestUtils.setField(user, "id", 1L);
        Device device = Device.builder()
                .user(user)
                .deviceCode("device-1")
                .deviceName("Device")
                .status(DeviceStatus.ACTIVE)
                .pairedAt(Instant.now())
                .build();
        ReflectionTestUtils.setField(device, "id", 10L);
        LocalDate date = LocalDate.of(2026, 3, 16);

        when(deviceRepository.findByStatus(DeviceStatus.ACTIVE)).thenReturn(List.of(device));
        when(bioSignalRepository.countByUserIdAndRecordedDateKr(1L, date)).thenReturn(0L);
        when(alertRepository.findTopByUserIdAndTypeAndResolvedFalseOrderByOccurredAtDesc(1L, AlertType.UPLOAD_MISSING))
                .thenReturn(Optional.empty());

        alertService.createUploadMissingAlerts(date);

        verify(alertRepository).save(any());
    }
}
