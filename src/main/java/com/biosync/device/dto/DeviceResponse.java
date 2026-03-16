package com.biosync.device.dto;

import com.biosync.device.domain.DeviceStatus;
import java.time.Instant;

public record DeviceResponse(
        Long deviceId,
        String deviceCode,
        String deviceName,
        String manufacturer,
        String model,
        DeviceStatus status,
        Instant pairedAt
) {
}
