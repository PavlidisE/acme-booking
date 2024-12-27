package com.github.pavlidise.acmebooking.model.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;

@Validated
public record BookingDTO(
        @NotBlank String userEmail,

        @Future String bookingStartDate,

        @Positive int numberOfHours
) {
}
