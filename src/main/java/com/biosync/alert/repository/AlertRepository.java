package com.biosync.alert.repository;

import com.biosync.alert.domain.Alert;
import com.biosync.alert.domain.AlertSeverity;
import com.biosync.alert.domain.AlertType;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRepository extends JpaRepository<Alert, Long> {

    Page<Alert> findByUserId(Long userId, Pageable pageable);

    Page<Alert> findByUserIdAndSeverity(Long userId, AlertSeverity severity, Pageable pageable);

    Page<Alert> findByUserIdAndOccurredAtBetween(Long userId, Instant from, Instant to, Pageable pageable);

    Page<Alert> findByUserIdAndSeverityAndOccurredAtBetween(
            Long userId,
            AlertSeverity severity,
            Instant from,
            Instant to,
            Pageable pageable
    );

    Page<Alert> findByResolved(boolean resolved, Pageable pageable);

    Page<Alert> findBySeverity(AlertSeverity severity, Pageable pageable);

    Page<Alert> findBySeverityAndResolved(AlertSeverity severity, boolean resolved, Pageable pageable);

    Optional<Alert> findTopByUserIdAndTypeAndResolvedFalseOrderByOccurredAtDesc(Long userId, AlertType type);

    List<Alert> findTop10ByUserIdOrderByOccurredAtDesc(Long userId);

    long countBySeverityAndOccurredAtBetween(AlertSeverity severity, Instant from, Instant to);
}
