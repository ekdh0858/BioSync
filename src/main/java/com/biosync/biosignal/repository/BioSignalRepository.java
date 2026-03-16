package com.biosync.biosignal.repository;

import com.biosync.biosignal.domain.BioSignal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BioSignalRepository extends JpaRepository<BioSignal, Long> {

    Page<BioSignal> findByUserIdAndRecordedAtBetween(Long userId, Instant from, Instant to, Pageable pageable);

    Page<BioSignal> findByUserIdAndDeviceIdAndRecordedAtBetween(
            Long userId,
            Long deviceId,
            Instant from,
            Instant to,
            Pageable pageable
    );

    List<BioSignal> findByUserIdAndRecordedDateKr(Long userId, LocalDate recordedDateKr);

    long countByUserIdAndRecordedDateKr(Long userId, LocalDate recordedDateKr);

    Optional<BioSignal> findTopByUserIdOrderByRecordedAtDesc(Long userId);
}
