package com.biosync.alert.domain;

import com.biosync.biosignal.domain.BioSignal;
import com.biosync.common.domain.BaseTimeEntity;
import com.biosync.device.domain.Device;
import com.biosync.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "alerts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Alert extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id")
    private Device device;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "biosignal_id")
    private BioSignal bioSignal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertSeverity severity;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private Instant occurredAt;

    @Column(nullable = false)
    private boolean resolved;

    private Instant resolvedAt;

    @Builder
    private Alert(
            User user,
            Device device,
            BioSignal bioSignal,
            AlertType type,
            AlertSeverity severity,
            String message,
            Instant occurredAt,
            boolean resolved,
            Instant resolvedAt
    ) {
        this.user = user;
        this.device = device;
        this.bioSignal = bioSignal;
        this.type = type;
        this.severity = severity;
        this.message = message;
        this.occurredAt = occurredAt;
        this.resolved = resolved;
        this.resolvedAt = resolvedAt;
    }

    public void resolve(Instant resolvedAt) {
        this.resolved = true;
        this.resolvedAt = resolvedAt;
    }
}
