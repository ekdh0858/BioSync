package com.biosync.summary.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.biosync.alert.repository.AlertRepository;
import com.biosync.biosignal.domain.BioSignal;
import com.biosync.biosignal.service.BioSignalService;
import com.biosync.device.domain.Device;
import com.biosync.device.domain.DeviceStatus;
import com.biosync.summary.domain.DailySummary;
import com.biosync.summary.repository.DailySummaryRepository;
import com.biosync.user.domain.Role;
import com.biosync.user.domain.User;
import com.biosync.user.domain.UserStatus;
import com.biosync.user.repository.UserRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class SummaryServiceTest {

    @Mock
    private DailySummaryRepository dailySummaryRepository;

    @Mock
    private BioSignalService bioSignalService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AlertRepository alertRepository;

    @InjectMocks
    private SummaryService summaryService;

    @Test
    void shouldCalculateDailySummary() {
        User user = user();
        Device device = device(user);
        LocalDate date = LocalDate.of(2026, 3, 16);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bioSignalService.findByUserAndDate(1L, date)).thenReturn(List.of(
                signal(user, device, "2026-03-16T01:00:00Z", 70, 100, 30, 20),
                signal(user, device, "2026-03-16T02:00:00Z", 90, 200, 40, 40)
        ));
        when(dailySummaryRepository.findByUserIdAndSummaryDate(1L, date)).thenReturn(Optional.empty());
        when(dailySummaryRepository.save(any(DailySummary.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(dailySummaryRepository.findTop3ByUserIdAndSummaryDateBeforeOrderBySummaryDateDesc(1L, date)).thenReturn(List.of());

        DailySummary saved = summaryService.recalculateSummary(1L, date);

        assertThat(saved.getAverageHeartRate()).isEqualTo(80);
        assertThat(saved.getMaxHeartRate()).isEqualTo(90);
        assertThat(saved.getMinHeartRate()).isEqualTo(70);
        assertThat(saved.getTotalSteps()).isEqualTo(300);
        assertThat(saved.getTotalSleepMinutes()).isEqualTo(70);
        assertThat(saved.getAverageStressLevel()).isEqualTo(30);
        assertThat(saved.getSignalCount()).isEqualTo(2);
    }

    @Test
    void shouldCreateLowActivityAlertWhenStepsDrop() {
        User user = user();
        Device device = device(user);
        LocalDate date = LocalDate.of(2026, 3, 16);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bioSignalService.findByUserAndDate(1L, date)).thenReturn(List.of(
                signal(user, device, "2026-03-16T01:00:00Z", 70, 100, 30, 20)
        ));
        when(dailySummaryRepository.findByUserIdAndSummaryDate(1L, date)).thenReturn(Optional.empty());
        when(dailySummaryRepository.save(any(DailySummary.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(dailySummaryRepository.findTop3ByUserIdAndSummaryDateBeforeOrderBySummaryDateDesc(1L, date)).thenReturn(List.of(
                previousSummary(user, date.minusDays(1), 1000),
                previousSummary(user, date.minusDays(2), 1200),
                previousSummary(user, date.minusDays(3), 800)
        ));
        when(alertRepository.findTopByUserIdAndTypeAndResolvedFalseOrderByOccurredAtDesc(1L, com.biosync.alert.domain.AlertType.LOW_ACTIVITY))
                .thenReturn(Optional.empty());

        summaryService.recalculateSummary(1L, date);

        verify(alertRepository).save(any());
    }

    private User user() {
        User user = User.builder()
                .email("user@example.com")
                .passwordHash("encoded")
                .name("Tester")
                .role(Role.USER)
                .status(UserStatus.ACTIVE)
                .build();
        ReflectionTestUtils.setField(user, "id", 1L);
        return user;
    }

    private Device device(User user) {
        Device device = Device.builder()
                .user(user)
                .deviceCode("device-1")
                .deviceName("Device")
                .status(DeviceStatus.ACTIVE)
                .pairedAt(Instant.now())
                .build();
        ReflectionTestUtils.setField(device, "id", 10L);
        return device;
    }

    private BioSignal signal(User user, Device device, String recordedAt, Integer heartRate, Integer steps, Integer sleep, Integer stress) {
        Instant instant = Instant.parse(recordedAt);
        return BioSignal.builder()
                .user(user)
                .device(device)
                .recordedAt(instant)
                .recordedDateKr(instant.atZone(ZoneId.of("Asia/Seoul")).toLocalDate())
                .heartRate(heartRate)
                .stepCount(steps)
                .sleepMinutes(sleep)
                .stressLevel(stress)
                .uploadRequestId("upload-1")
                .build();
    }

    private DailySummary previousSummary(User user, LocalDate date, int totalSteps) {
        return DailySummary.builder()
                .user(user)
                .summaryDate(date)
                .averageHeartRate(70)
                .maxHeartRate(80)
                .minHeartRate(60)
                .totalSteps(totalSteps)
                .totalSleepMinutes(400)
                .averageStressLevel(20)
                .signalCount(10)
                .aggregatedAt(Instant.now())
                .build();
    }
}
