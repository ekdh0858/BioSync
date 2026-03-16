package com.biosync.device.domain;

import com.biosync.common.domain.BaseTimeEntity;
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
@Table(name = "devices")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Device extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true)
    private String deviceCode;

    @Column(nullable = false)
    private String deviceName;

    private String manufacturer;

    private String model;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeviceStatus status;

    private Instant pairedAt;

    @Builder
    private Device(
            User user,
            String deviceCode,
            String deviceName,
            String manufacturer,
            String model,
            DeviceStatus status,
            Instant pairedAt
    ) {
        this.user = user;
        this.deviceCode = deviceCode;
        this.deviceName = deviceName;
        this.manufacturer = manufacturer;
        this.model = model;
        this.status = status;
        this.pairedAt = pairedAt;
    }
}
