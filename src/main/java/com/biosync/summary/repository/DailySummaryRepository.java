package com.biosync.summary.repository;

import com.biosync.summary.domain.DailySummary;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailySummaryRepository extends JpaRepository<DailySummary, Long> {

    Optional<DailySummary> findByUserIdAndSummaryDate(Long userId, LocalDate summaryDate);

    List<DailySummary> findTop3ByUserIdAndSummaryDateBeforeOrderBySummaryDateDesc(Long userId, LocalDate summaryDate);
}
