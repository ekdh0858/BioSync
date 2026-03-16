package com.biosync.device.service;

import com.biosync.common.exception.ApiException;
import com.biosync.device.domain.Device;
import com.biosync.device.domain.DeviceStatus;
import com.biosync.device.dto.DeviceRegisterRequest;
import com.biosync.device.dto.DeviceResponse;
import com.biosync.device.repository.DeviceRepository;
import com.biosync.user.domain.User;
import com.biosync.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;

    public DeviceResponse register(Long userId, DeviceRegisterRequest request) {
        if (deviceRepository.existsByDeviceCode(request.deviceCode())) {
            throw new ApiException("CONFLICT", "Device code already exists", HttpStatus.CONFLICT);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("NOT_FOUND", "User not found", HttpStatus.NOT_FOUND));

        Device device = deviceRepository.save(Device.builder()
                .user(user)
                .deviceCode(request.deviceCode())
                .deviceName(request.deviceName())
                .manufacturer(request.manufacturer())
                .model(request.model())
                .status(DeviceStatus.ACTIVE)
                .pairedAt(request.pairedAt())
                .build());

        return toResponse(device);
    }

    @Transactional(readOnly = true)
    public List<DeviceResponse> getDevices(Long userId) {
        return deviceRepository.findByUserId(userId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public Device getOwnedDevice(Long deviceId, Long userId) {
        return deviceRepository.findByIdAndUserId(deviceId, userId)
                .orElseThrow(() -> new ApiException("NOT_FOUND", "Device not found", HttpStatus.NOT_FOUND));
    }

    private DeviceResponse toResponse(Device device) {
        return new DeviceResponse(
                device.getId(),
                device.getDeviceCode(),
                device.getDeviceName(),
                device.getManufacturer(),
                device.getModel(),
                device.getStatus(),
                device.getPairedAt());
    }
}
