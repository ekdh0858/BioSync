package com.biosync.biosignal.domain;

import com.biosync.common.domain.BaseTimeEntity;
import com.biosync.device.domain.Device;
import com.biosync.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "biosignals")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BioSignal extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @Column(nullable = false)
    private Instant recordedAt;

    @Column(nullable = false)
    private LocalDate recordedDateKr;

    private Integer heartRate;

    private Integer stepCount;

    private Integer sleepMinutes;

    private Integer stressLevel;

    @Column(length = 64)
    private String uploadRequestId;

    @Builder
    private BioSignal(
            User user,
            Device device,
            Instant recordedAt,
            LocalDate recordedDateKr,
            Integer heartRate,
            Integer stepCount,
            Integer sleepMinutes,
            Integer stressLevel,
            String uploadRequestId
    ) {
        this.user = user;
        this.device = device;
        this.recordedAt = recordedAt;
        this.recordedDateKr = recordedDateKr;
        this.heartRate = heartRate;
        this.stepCount = stepCount;
        this.sleepMinutes = sleepMinutes;
        this.stressLevel = stressLevel;
        this.uploadRequestId = uploadRequestId;
    }
}
