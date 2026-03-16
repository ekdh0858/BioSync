package com.biosync.device.repository;

import com.biosync.device.domain.Device;
import com.biosync.device.domain.DeviceStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device, Long> {

    boolean existsByDeviceCode(String deviceCode);

    List<Device> findByUserId(Long userId);

    List<Device> findByStatus(DeviceStatus status);

    Optional<Device> findByIdAndUserId(Long id, Long userId);

    long countByStatus(DeviceStatus status);
}
