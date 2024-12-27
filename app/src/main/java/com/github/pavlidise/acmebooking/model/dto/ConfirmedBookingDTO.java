package com.github.pavlidise.acmebooking.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ConfirmedBookingDTO(
        @NotBlank
        String roomName,

        @NotBlank
        String userEmail,

        @NotNull
        LocalDateTime bookingStartTime,

        @NotNull
        LocalDateTime bookingEndTime
) {
}
