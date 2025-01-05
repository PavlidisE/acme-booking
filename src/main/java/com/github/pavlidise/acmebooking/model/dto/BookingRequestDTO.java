package com.github.pavlidise.acmebooking.model.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

@Validated
public record BookingRequestDTO(

        @NotBlank String userEmail,

        @NotBlank String roomName,

        @Future
        LocalDateTime bookingStartDateTime,

        @Min(1) int numberOfHours
) {
}
