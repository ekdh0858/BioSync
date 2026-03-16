package com.biosync.biosignal.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record BioSignalBatchUploadRequest(
        @NotNull Long deviceId,
        @NotBlank String uploadRequestId,
        @NotEmpty List<@Valid BioSignalItemRequest> signals
) {
}
