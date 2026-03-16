package com.biosync.summary.controller;

import com.biosync.common.api.ApiResponse;
import com.biosync.security.AuthenticatedUser;
import com.biosync.summary.service.SummaryService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/summaries")
@RequiredArgsConstructor
public class SummaryController {

    private final SummaryService summaryService;

    @GetMapping("/daily")
    public ApiResponse<?> getDailySummary(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestParam LocalDate date
    ) {
        return ApiResponse.success(summaryService.getDailySummary(authenticatedUser.getId(), date));
    }
}
