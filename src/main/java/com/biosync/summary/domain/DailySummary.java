package com.biosync.summary.domain;

import com.biosync.common.domain.BaseTimeEntity;
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
@Table(name = "daily_summaries")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailySummary extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate summaryDate;

    private Integer averageHeartRate;

    private Integer maxHeartRate;

    private Integer minHeartRate;

    @Column(nullable = false)
    private Integer totalSteps;

    @Column(nullable = false)
    private Integer totalSleepMinutes;

    private Integer averageStressLevel;

    @Column(nullable = false)
    private Integer signalCount;

    @Column(nullable = false)
    private Instant aggregatedAt;

    @Builder
    private DailySummary(
            User user,
            LocalDate summaryDate,
            Integer averageHeartRate,
            Integer maxHeartRate,
            Integer minHeartRate,
            Integer totalSteps,
            Integer totalSleepMinutes,
            Integer averageStressLevel,
            Integer signalCount,
            Instant aggregatedAt
    ) {
        this.user = user;
        this.summaryDate = summaryDate;
        this.averageHeartRate = averageHeartRate;
        this.maxHeartRate = maxHeartRate;
        this.minHeartRate = minHeartRate;
        this.totalSteps = totalSteps;
        this.totalSleepMinutes = totalSleepMinutes;
        this.averageStressLevel = averageStressLevel;
        this.signalCount = signalCount;
        this.aggregatedAt = aggregatedAt;
    }

    public void update(
            Integer averageHeartRate,
            Integer maxHeartRate,
            Integer minHeartRate,
            Integer totalSteps,
            Integer totalSleepMinutes,
            Integer averageStressLevel,
            Integer signalCount,
            Instant aggregatedAt
    ) {
        this.averageHeartRate = averageHeartRate;
        this.maxHeartRate = maxHeartRate;
        this.minHeartRate = minHeartRate;
        this.totalSteps = totalSteps;
        this.totalSleepMinutes = totalSleepMinutes;
        this.averageStressLevel = averageStressLevel;
        this.signalCount = signalCount;
        this.aggregatedAt = aggregatedAt;
    }
}
