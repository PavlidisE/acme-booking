package com.github.pavlidise.acmebooking.model.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

@Validated
public record BookingRequestDTO(
        @NotBlank String userEmail,

        @Future
        LocalDateTime bookingStartDateTime,

        @Positive int numberOfHours
) {
}
