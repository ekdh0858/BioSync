package com.biosync.device.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record DeviceRegisterRequest(
        @NotBlank @Size(max = 100) String deviceCode,
        @NotBlank @Size(max = 100) String deviceName,
        @Size(max = 100) String manufacturer,
        @Size(max = 100) String model,
        Instant pairedAt
) {
}
