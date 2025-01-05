package com.github.pavlidise.acmebooking.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record ConfirmedBookingDTO(
        @NotBlank
        UUID uuid,

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
